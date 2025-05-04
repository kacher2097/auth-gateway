# Video Downloader API

This API allows you to download videos from YouTube and TikTok.

## Endpoints

### Download Video

```
POST /api/v1/tools/video-downloader
```

This endpoint automatically detects the platform (YouTube or TikTok) from the URL and downloads the video.

#### Request Body

```json
{
  "url": "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
  "format": "mp4",
  "quality": "high",
  "userId": "user123"
}
```

- `url` (required): The URL of the video to download
- `format` (optional): The format of the video (e.g., mp4, mp3)
- `quality` (optional): The quality of the video (high, low)
- `userId` (optional): User ID for tracking purposes

#### Response

```json
{
  "code": "00",
  "message": "SUCCESS",
  "data": {
    "downloadUrl": "http://localhost:8080/api/v1/tools/video-downloader/download/abc123/video.mp4",
    "title": "Video Title",
    "author": "Video Author",
    "duration": 180,
    "size": 10485760,
    "format": "mp4",
    "quality": "high",
    "thumbnailUrl": "https://example.com/thumbnail.jpg"
  }
}
```

### Download YouTube Video

```
POST /api/v1/tools/video-downloader/youtube
```

This endpoint specifically downloads videos from YouTube.

#### Request Body

Same as the general endpoint.

#### Response

Same as the general endpoint.

### Download TikTok Video

```
POST /api/v1/tools/video-downloader/tiktok
```

This endpoint specifically downloads videos from TikTok.

#### Request Body

Same as the general endpoint.

#### Response

Same as the general endpoint.

### Download File

```
GET /api/v1/tools/video-downloader/download/{folderId}/{fileName}
```

This endpoint downloads the actual video file.

- `folderId`: The folder ID containing the video
- `fileName`: The name of the video file

#### Response

The video file as a binary stream.

## Examples

### Download a YouTube Video

```bash
curl -X POST \
  http://localhost:8080/api/v1/tools/video-downloader \
  -H 'Content-Type: application/json' \
  -d '{
    "url": "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
    "quality": "high"
  }'
```

### Download a TikTok Video

```bash
curl -X POST \
  http://localhost:8080/api/v1/tools/video-downloader \
  -H 'Content-Type: application/json' \
  -d '{
    "url": "https://www.tiktok.com/@username/video/1234567890123456789",
    "quality": "high"
  }'
```

## Notes

- The API supports downloading videos from YouTube and TikTok only.
- For YouTube videos, you can specify the quality (high, low) and format (mp4, mp3).
- For TikTok videos, the quality and format options may not be applicable.
- The downloaded files are temporarily stored on the server and may be deleted after a certain period.
