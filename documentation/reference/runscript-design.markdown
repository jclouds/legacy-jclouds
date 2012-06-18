# Running a Script using SSH : implementation
When runScript/submitScript are used, the execution currently follows the following path

```
  RunScriptOnNode.Factory .runScript → create submit → submit
```

The `RunScripOnNode.Factory` is responsible for making the _real_ script from what the user supplied behind the scenes.

When that factory is used, it delegates to another one (`RunScriptOnNodeFactoryImpl.Factory`) -- depending on a few things. For example, is the script to be directly invoked?
If `RunScriptOptions.wrapInInitScript(false)` in the case of directly invoked `exec`, we don't mess with the script at all,  it is just executed directly.

If `RunScriptOptions.wrapInInitScript(true)` option is set (the default is true), then we set variables and wrap the supplied script in "init-script".

The code that handles customizing the script forrunning in the background is `RunScriptOnNodeAsInitScriptUsingSsh` particularly `RunScriptOnNodeAsInitScriptUsingSsh#createInitScript`
The name of the script will be set as specified by  `RunScriptOptions#getTaskName`.
