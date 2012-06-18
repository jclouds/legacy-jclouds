* Running a Script using SSH : implementation
When runScript/submitScript are used, the  code currently all goes in the following path

```
  RunScriptOnNode.Factory .runScript -> create submit -> submit
```

The `RunScripOnNode.Factory` is responsible for making the  "real"
 script from what the user supplied  behind the scenes.
 when that factory is used, it  delegates to another one depending on a few things  `RunScriptOnNodeFactoryImpl.Factory`  for example, is the script to be directly invoked?
(ex. RunScriptOptions.wrapInInitScript(false)  -->   exec in the case of directly invoked, we don't mess with
the script at all,  it is just executed directly.
If wrapInInitScript(true) option is set (the default is true), then we are setting variables.

The code that handles customizing the script for  running in the
background is `RunScriptOnNodeAsInitScriptUsingSsh` particularly `RunScriptOnNodeAsInitScriptUsingSsh#createInitScript`
The name of the script will be set as specified by  ` RunScriptOptions#getTaskName`.
