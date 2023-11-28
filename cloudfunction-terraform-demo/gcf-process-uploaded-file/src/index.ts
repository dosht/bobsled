import { cloudEvent, CloudEvent } from "@google-cloud/functions-framework"
import { StorageObjectData } from "@google/events/cloud/storage/v1/StorageObjectData"
import * as ffmpeg from "fluent-ffmpeg"
import { createClient } from "@supabase/supabase-js"
import * as fs from "fs"
import { Job } from "types"
import { Storage } from "@google-cloud/storage"
import * as path from "path"
import * as os from "os"
import pino from "pino"

const logger = pino()

logger.info("============= Starting Function ========================")

export async function gcf_process_uploaded_file(cloudEvent: CloudEvent<StorageObjectData>) {
  logger.info("---------------\nProcessing for ", cloudEvent.subject, "\n---------------")

  logger.info(`Processing audio file gs://${cloudEvent.data?.bucket}/${cloudEvent.data?.name}`)
  const bucketName = cloudEvent.data?.bucket
  const filePath = cloudEvent.data?.name

  if (bucketName == null || filePath == null) {
    logger.error(`Invalid even payload: bucketName: ${bucketName}, filePath: ${filePath}`)
    return
  }

  const storage = new Storage()
  const bucket = storage.bucket(bucketName)
  const file = bucket.file(filePath)

  // Create a temporary file path
  const tempFilePath = path.join(os.tmpdir(), path.basename(filePath))

  try {
    // Download the audio file to a temporary location
    await file.download({ destination: tempFilePath })
    // Calculate the duration using FFmpeg
    const durationInSeconds = await getAudioDuration(tempFilePath)
    // Print the duration (or perform any desired further processing)
    logger.info(`Audio duration: ${durationInSeconds} seconds`)
    const durationInMinutes: number = Math.ceil(durationInSeconds / 60)
    const costPerMinute: number = 0.0125
    const cost: number = durationInMinutes * costPerMinute
    logger.info(`Audio duration: ${durationInMinutes} minutes`)
    logger.info(`Audio transcriptiong cost: \$${cost}`)

    // Extract file information
    const urlParts = filePath.split("/")
    const userId = urlParts[0].split("=")[1]
    const jobId = parseInt(urlParts[1].split("=")[1])
    const fileName = urlParts[2]
    // Update job recrod
    logger.info(`Updating job: ${jobId}, userId: ${userId}, fileName: ${fileName}`)
    // const jobRepository = new JobRepository(supabase)
    const job = await updateJob(userId, jobId, {
      duration: durationInMinutes,
      cost: cost,
    })
    logger.info("Job updated: ", job)
    // Clean up the temporary file
    await deleteFile(tempFilePath)
  } catch (error) {
    logger.error({ error: error }, "Error processing audio")
  }
}

async function getAudioDuration(filePath: string): Promise<number> {
  return new Promise<number>((resolve, reject) => {
    ffmpeg.setFfmpegPath("")
    ffmpeg.ffprobe(filePath, (err, metadata) => {
      if (err) {
        reject(err)
      } else {
        const durationInSeconds = metadata.format.duration || 0
        resolve(durationInSeconds)
      }
    })
  })
}

async function deleteFile(filePath: string): Promise<void> {
  return new Promise<void>((resolve, reject) => {
    fs.unlink(filePath, (err) => {
      if (err) {
        reject(err)
      } else {
        resolve()
      }
    })
  })
}

async function updateJob(userId: string, jobId: number, updates: Partial<Job>): Promise<Job | null> {
  // Create supabase admin client using SUPABASE_URL, SUPABASE_ANON_KEY and SUPABASE_SERVICE_ROLE_KEY
  const supabaseUrl = process.env.SUPABASE_URL!
  const supabaseServiceRoleKey = process.env.SUPABASE_SERVICE_ROLE_KEY!
  const supabase = createClient(supabaseUrl, supabaseServiceRoleKey, {
    auth: {
      autoRefreshToken: false,
      persistSession: false,
    },
  })
  const adminAuthClient = supabase.auth.admin

  const { data, error } = await supabase.from("jobs").update(updates).eq("user_id", userId).eq("id", jobId).select().single()
  if (error) {
    logger.error(error)
    throw new Error("Failed to update the job")
  }
  return data || null
}

cloudEvent("gcf_process_uploaded_file", gcf_process_uploaded_file)
