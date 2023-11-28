import { http, Request, Response } from "@google-cloud/functions-framework"
import { GetSignedUrlConfig, Storage } from "@google-cloud/storage"
import { v4 as uuidv4 } from "uuid"

async function gcf_get_upload_url(req: Request, res: Response) {
  console.debug("create signed url for", req.body.fileName)

  const bucketName = "transgate-audios"

  const fileName = req.body.fileName || uuidv4() // Use a random filename if not provided in the request query
  const permission = req.body.permission || "read"

  const storage = new Storage()

  const options: Options = {
    version: "v4",
    expires: Date.now() + 15 * 60 * 1000, // URL expiration time (15 minutes from now)
  }

  if (permission === "read") {
    options.predefinedAcl = "publicRead"
    options.action = "read"
  } else if (permission === "write") {
    options.predefinedAcl = "bucket-owner-full-control"
    options.action = "write"
    options.contentType = "audio/mpeg"
  }

  try {
    const [response] = await storage
      .bucket(bucketName)
      .file(fileName)
      .getSignedUrl((options as unknown) as GetSignedUrlConfig)

    console.log("SUCCESS")
    res.status(200).send(response)
  } catch (err) {
    console.error("Error generating signed URL:", err)
    res.status(500).send("Error generating signed URL")
  }
}

type Options = { [key: string]: string | number }

http("gcf_get_upload_url", gcf_get_upload_url)
