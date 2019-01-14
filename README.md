# android-speech2text-watson
android speech2text app using ibm waston api

# How to use
## Get the Service Credentials

1. Sign up for an IBM Cloud Account
1. Create an instance of the Waston service you want to use and get your credentials
1. Copy the `apikey` and `url` value from the instance

## Add the Credentials

1. Create an file `app/src/main/res/values/credential.xml`
1. Copy the following lines into it

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="speech_text_iam_apikey">{apikey}</string>
    <string name="speech_text_url">{url}</string>
</resources>
```
where `{apikey}` and `{url}` are the values copied from the [official site](https://cloud.ibm.com/)

1. Prepare the audio file and modify the path located in `app/src/main/java/com/example/ethen/myapplication2/MainActivity.java` line 109 and line 110
