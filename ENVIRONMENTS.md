| NAME                      | REQUIRED | DESCRIPTION                           | DEFAULT VALUE          |
|---------------------------|----------|---------------------------------------|------------------------|
| `PORT`                    | false    | Port number to listen on              | 8080                   |
| `HOST_URI`                | true     | URI to get domain where deployed      | -                      |
| `EMAIL`                   | false    | Email of current service              | taras@kovaliv.dev      |
| `EMAIL_NAME`              | false    | Name for email as sender              | KOVALIV.DEV            |
| `EMAIL_PASSWORD`          | false    | Password for email account            | -                      |
| `DEV_KOV_PROPERTIES_PATH` | false    | Path to .properties file              | application.properties |
| `ENCRYPTION_KEY`          | false    | Key to encrypt data using CryptoUtils | -                      |
| `REDIS_HOST`              | false    | Redis host                            | -                      |
| `REDIS_PORT`              | false    | Redis port                            | 6379                   |
| `REDIS_PASSWORD`          | false    | Redis password                        | -                      |