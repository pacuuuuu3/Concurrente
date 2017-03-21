/**
 *
 * @author Felipe N.
 */
public class Semaforo {
    
    protected int val;
    protected final Object mutex = new Object();
    
    public Semaforo(int iniVal) {
        if (iniVal < 0 ){
            throw new IllegalArgumentException("Valor Negativo");
        }
        this.val = iniVal;
    }
    
    public Semaforo() {
        this(1);
    }
    
    public void p() {
        synchronized(mutex) {
            while (val <= 0) {
                try {
                    mutex.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            val--;
        }
    }
    
    public void v() {
        synchronized(mutex) {
            val++;
            mutex.notify();
        }
    }
    
    public void reset(int val) {
        this.val = val;
    }
    
}
