# msoffice-mimetype-detector

Lightweight MS Office mime type detector library, written in Kotlin. Compatible with Java>=8 and Android.

### Why?

Apache Tika is the best way to detect file mime types. Unfortunately on MS Office documents, it gives us a generic ``application/x-tika-msoffice`` mime type.

This library can detect docx, xlsx, pptx, doc, xls, ppt files.

It is based on [this](https://stackoverflow.com/a/48318648) stack overflow answer.

### How to import it (Gradle)?

```
repositories {
    maven { url "https://maven.andob.info/repository/open_source" }
}
```

```
dependencies {
    implementation 'ro.andob.msoffice:mime-type-detector:1.0.4'
}
```

### How to use it?

```
public String detectMimeType(File file)
{
    //detect msoffice mime type
    String mimeType = MicrosoftOfficeFileMimeTypeDetector.getMimeType(file);

    if (mimeType==null)
    {
        //not a msoffice document, get mime type with tika
        mimeType = new AutoDetectParser().getDetector()
            .detect(new FileInputStream(file), new Metadata())
            .toString();
    }

    return mimeType;
}
```

### License

```
Copyright 2020 Andrei Dobrescu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
