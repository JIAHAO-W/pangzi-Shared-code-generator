package com.yupi.cli.example;

import picocli.CommandLine;

@CommandLine.Command(name = "ASCIIArt" , version = "ASCIIArt 1.0",mixinStandardHelpOptions = true)
public class ASCIIArt implements Runnable{

    @CommandLine.Option(names = {"-s","--font-size"},description = "Font size")
    int fontSize = 19;

    @CommandLine.Parameters(paramLabel = "<word>",defaultValue = "Hello,picocli",
            description = "Words to be translated into ASCII art.")
    private String[] words = {"hello,","picocli","1","2"};

    @Override
    public void run() {
        //自己实现业务
        System.out.println("fontSize = "+ fontSize);
        System.out.println("words = "+String.join(",",words));
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ASCIIArt()).execute(args);
        System.out.println(exitCode);
    }
}
