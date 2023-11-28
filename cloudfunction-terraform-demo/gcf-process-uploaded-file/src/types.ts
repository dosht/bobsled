export interface UserDetails {
  id: string /* primary key */
  email: string
  first_name: string
  last_name: string
  full_name?: string
  avatar_url?: string
}

type JobStatus = "draft" | "not_started" | "pending" | "running" | "completed" | "failed" | "canceled"

export interface Job {
  id?: number
  user_id: string
  job_name: string | null
  job_status: string
  language: string | null
  speakers: boolean | null
  file_path?: string | null
  duration?: number | null
  cost?: number | null
  worker_selector?: string | null
  request_time?: number | null
  assigned_worker?: number | null
  /* TODO
  transcriptionUrl
  transcriptionStatus
  transcriptionError
  transcriptionDuration
  transcriptionCost
  transcriptionFileName
  */
}

export interface TransctiptLine {
  id: number
  job_id: number
  user_id: string
  start_time: string
  end_time: string
  line: string
  file: string
}

export interface TranscriptLine {
  id: number
  job_id: number
  user_id: string
  start_time: string
  end_time: string
  line: string
  file: string
}
