package com.xeelux.droplets.test;

import com.xeelux.droplets.core.MainBase;

public final class Main extends MainBase {

    public static void main(String[] args) {
        // creating an instance of Main class...
        final var main = new Main();
        // calling the run() method...
        main.run(args, TestApplication.class);
    }
}
