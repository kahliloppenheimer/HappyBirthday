import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;


public class HappyBirthday {

    public static final String pos0 = "<(^_^<)";
    public static final String pos1 = "<(^_^)>";
    public static final String pos2 = "(>^_^)>";
    public static int pos = 0;

    public static void main(String[] args) throws LineUnavailableException {
        final AudioFormat af =
            new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, true);
        SourceDataLine line = AudioSystem.getSourceDataLine(af);
        line.open(af, Note.SAMPLE_RATE);
        line.start();
        Note[] notes = {Note.A4, Note.A4, Note.B4, Note.A4, Note.D4, Note.C4$, Note.REST,
                        Note.A4, Note.A4, Note.B4, Note.A4, Note.E4, Note.D4, Note.REST, 
                        Note.A4, Note.A4, Note.A5, Note.F4$, Note.D4, Note.C4$, Note.B4, Note.REST,
                        Note.G4, Note.G4, Note.F4$, Note.D4, Note.E4, Note.D4};
        int[] lengths = {500, 500, 1000, 1000, 1000, 1200, 800,
                         500, 500, 1000, 1000, 1000, 1500, 1000,
                         600, 600, 1100, 1100, 1100, 1100, 1100, 1000,
                         1000, 1000, 1500, 1500, 2000, 3000};
        for  (int i = 0; i < notes.length; i++) {
            play(line, notes[i], lengths[i]);
            play(line, Note.REST, 10);
            cycleThrough(1, line);
        }
        line.drain();
        line.close();
    }

    private static void play(SourceDataLine line, Note note, int ms) {
        ms = Math.min(ms, Note.SECONDS * 1000);
        int length = Note.SAMPLE_RATE * ms / 1000;
        int count = line.write(note.data(), 0, length);
    }

    private static void cycleThrough(int numTimes, SourceDataLine line) {
        try {
            for(int i = 0; i < 3 * numTimes; i++) {
                shiftPos();
                Thread.sleep(75);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    private static void shiftPos() {
        pos = (pos + 1) % 3;
        clearScreen(); 
        if(pos == 0) System.out.println(pos0);
        else if (pos == 1) System.out.println(pos1);
        else if (pos == 2) System.out.println(pos2);
    }
    private static void clearScreen() {
        for(int i = 0; i < 100; i++) {
            System.out.println();
        }
    }

}
enum Note {

    REST, A4, A4$, B4, C4, C4$, D4, D4$, E4, F4, F4$, G4, G4$, A5;
    public static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
    public static final int SECONDS = 2;
    private byte[] sin = new byte[SECONDS * SAMPLE_RATE];

    Note() {
        int n = this.ordinal();
        if (n > 0) {
            double exp = ((double) n - 1) / 12d;
            double f = 440d * Math.pow(2d, exp);
            for (int i = 0; i < sin.length; i++) {
                double period = (double)SAMPLE_RATE / f;
                double angle = 2.0 * Math.PI * i / period;
                sin[i] = (byte)(Math.sin(angle) * 127f);
            }
        }
    }

    public byte[] data() {
        return sin;
    }
}
