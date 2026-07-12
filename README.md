# poja-fs-template — S3 file storage for Spring Boot

A [Poja](https://poja.io) starter template with **AWS S3 file storage** pre-configured. Upload, download, and generate presigned URLs from your Spring Boot code — no S3 setup required.

→ **[Full guide on docs.poja.io](https://docs.poja.io/docs/hello-world-but-with-persistent-memories)**

Or hit the `Deploy to Poja` button to **deploy this template on your account** : 


[![Deploy on Poja](https://img.shields.io/badge/Deploy%20On%20Poja-007BFF?style=for-the-badge)](https://console.poja.io/applications/create/clone/?templateId=9e25599a-cf73-4558-8697-b22273a6171b)

### What you get

Poja injects a ready-to-use `BucketComponent` with four methods:

```java
@Service
@AllArgsConstructor
public class MyService {
  private final BucketComponent bucketComponent;

  public String uploadAndShare(File file, String key) {
    bucketComponent.upload(file, key);
    return bucketComponent.presign(key, Duration.ofMinutes(5)).toString();
  }
}
```

| Method | What it does |
|---|---|
| `upload(file, key)` | Uploads a file to the app's S3 bucket |
| `download(key)` | Downloads a file by key |
| `presign(key, duration)` | Returns a time-limited presigned URL |
| `getBucketName()` | Returns the bucket name |

A `/health/bucket` endpoint is added automatically once file storage is enabled.

> Part of the [Poja plarform](https://poja.io) — deploy Spring Boot in minutes.
