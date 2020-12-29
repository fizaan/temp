package cofeegb;

/*
	Thanks to:
	https://blog.rekawek.eu/2017/02/09/coffee-gb/
*/

public class Main {

	public static void main(String[] args) throws Exception {
		System.setProperty("apple.awt.application.name", "Coffee GB");
        Emulator emu = new Emulator();
        emu.run();
        while(true) {
        	emu.spitter();
        	sleep(1);
        }
	}

	private static void sleep(int s) {
		Thread.currentThread();
		try {
			Thread.sleep(s);
		}
		catch(Exception e) {

		}
	}

}
