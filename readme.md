
<h1 align="center">
  <br>
  <a href="/static/img/logo.png"><img src="/static/img/logo.png" alt="Markdownify" width="200"></a>
  <br>
  Scampi Search 2.0
  <br>
</h1>

<h4 align="center">A minimal search engine build on <a href="https://codepen.io/vangato/details/LvWjxd" target="_blank">SERP</a>.</h4>

<p align="center">
  <a href="#key-features">Key Features</a> •
  <a href="#how-to-use">How To Use</a> •
</p>

![screenshot](/static/img/scr1.png)

## Key Features

* Image search
  - Instantly see all images of a crawled website.
* Own integrated cralwer (2.0)
* Nothing is crawled trough googles API
* Database managment
* Cross platform
  - Windows, macOS and Linux ready.

## How To Use

To clone and run this application, you'll need [Git](https://git-scm.com) and [Maven](https://maven.apache.org/) installed on your computer. From your command line:

```bash
# Clone the repository
$ git clone https://github.com/MarcPlaying/webSearch

# Go to our repository
$ cd webSearch

# Install dependencies
$ mvn install
```

> **Note**
> Now you can run both jar files but the crawler requires a csv you can download it [here](https://www.domcop.com/files/top/top10milliondomains.csv.zip) then you need to split the file how much threads you have
#### 1 Thread 0.csv
#### 5 Thread 0.csv 1.csv 2.csv ... 4.csv
