# INTERLIS validation commands

Compile model:

```bash
java -jar /Users/stefan/apps/ili2c-5.6.8/ili2c.jar   --modeldir "src/test/data/interlis1/models;https://models.interlis.ch"   MinimalArea_V1
```

Validate transfer:

```bash
java -jar /Users/stefan/apps/ilivalidator-1.15.0/ilivalidator-1.15.0.jar   --modeldir "src/test/data/interlis1/models;https://models.interlis.ch"   --models MinimalArea_V1   src/test/data/interlis1/transfers/minimal-area.itf
```
