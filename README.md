# Review Crawler

A Java based tool that extracts the user reviews for a given app from the related online store. It reads the list of apps to mine from a csv file and write the corresponding review in a csv file of output.

### Configuration

The tool must be setted through its configuration file. You have to specify the input and the output file name. The date of last review is automatically updated.

### Parameters

Currently the only permitted parameter is `extractor`. With 

```
extractor=reviews
```

the tool will mine the reviews of the given apps. Instead, with

```
extractor=info
```

the tool will mine the info about an app, like the current version and the last update time.

### Export the mined app

In order to query the DB and export the review, you have to run:

```
extractor=export
```
The reviews for the app specified in the `input_file` parameter of the `configuration.properties` will be extracted.
