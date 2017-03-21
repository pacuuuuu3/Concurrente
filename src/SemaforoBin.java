/**
 *
 * @author Felipe N.
 */
public class SemaforoBin extends Semaforo {
    
    public SemaforoBin(int iniVal) {
        super(iniVal);
        if (iniVal > 1) {
            throw new IllegalArgumentException("iniVal > 1");
        }
    }
    
    public SemaforoBin() {
        super();
    }
    
    @Override
    public void v() {
        synchronized(mutex) {
            super.v();
            if (val > 1) {
                val = 1;
            }
        }
    }
    
}
