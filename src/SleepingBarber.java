
/**
 *
 * @author Felipe Navarrete Córdova <felipejnc@gmail.com>
 */
public class SleepingBarber {
    
    static class Barbero extends Thread {
        
        private Barberia barberia;
        private String nombre;
        
        public Barbero(String nombre, Barberia barberia) {
            this.nombre = nombre;
            this.barberia = barberia;
        }

        @Override
        public void run() {
            while (true) {
                barberia.cortarCabello();
            }
        }
        
    }
    
    static class Cliente extends Thread {
        
        private int clienteId;
        private Barberia barberia;

        public Cliente(int clienteId, Barberia barberia) {
            this.clienteId = clienteId;
            this.barberia = barberia;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    long nap = (long) (Math.random() * 20000);
                    System.out.printf("Tiempo: %d, Cliente %d. Creciendo cabello por %d ms\n",
                            System.currentTimeMillis(), clienteId, nap);
                    Thread.sleep(nap);
                    System.out.printf("Tiempo: %d, Cliente %d necesita corte de cabello\n",
                            System.currentTimeMillis(), clienteId);
                    barberia.solicitarCorte(clienteId);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
    }
    
    static class Barberia {
        
        private int numSillas;
        private Semaforo llenoSem;
        private Semaforo barberoSem;
        private SemaforoBin mutex;
        private Semaforo cortandoSem;
        private int esperando;
        
        public Barberia(int numSillas) {
            this.numSillas = numSillas;
            llenoSem = new Semaforo(0);
            barberoSem = new Semaforo(1);
            mutex = new SemaforoBin(1);
            cortandoSem = new Semaforo(0);
        }

        private void cortarCabello() {
            System.out.printf("Tiempo: %d, BARBERO DISPONIBLE... esperando cliente\n",
                    System.currentTimeMillis());
            llenoSem.p();
            mutex.p();
            esperando--;
            System.out.printf("Tiempo: %d, EL BARBERO TIENE CLIENTE. Clientes en espera: %d\n",
                    System.currentTimeMillis(), esperando);
            mutex.v();
            cortandoSem.p();
            barberoSem.v();
        }

        private void solicitarCorte(int clienteId) {
            mutex.p();
            if (esperando < numSillas) {
                esperando++;
                System.out.printf("Tiempo: %d, Cliente %d en la barbería. Esperando: %d\n",
                        System.currentTimeMillis(), clienteId, esperando);
                llenoSem.v();
                mutex.v();
                barberoSem.p();
                System.out.printf("Tiempo: %d, Cliente %d está siendo atendido\n",
                        System.currentTimeMillis(), clienteId);
                try {
                    Thread.sleep((long) (Math.random() * 10000));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                System.out.printf("Tiempo: %d, Cliente %d ha sido atendido\n",
                        System.currentTimeMillis(), clienteId);
                cortandoSem.v();
            } else {
                System.out.printf("Tiempo: %d, Cliente %s, barbería llena, regresará después\n",
                        System.currentTimeMillis(), clienteId);
                mutex.v();
            }
        }
    }
    
    public static void main(String... args) {
        int numSillas = 2;
        int numClientes = 5;
        
        for (int x = 0; x < args.length; x++) {
            try {
                if (args[x].startsWith("-c")) {
                    numClientes = Integer.parseInt(args[x].substring(1));
                    if (numClientes <= 0) {
                        numClientes = 2;
                    }
                } else if (args[x].startsWith("-s")) {
                    numSillas = Integer.parseInt(args[x].substring(1));
                    if (numSillas <= 0) {
                        numSillas = 2;
                    }
                }
            } catch (Exception ex) {}
        }
        
        if (args.length == 1) {
            try {
                
            } catch (NumberFormatException ex) {
            }
        }
        
        
        Barberia barb = new Barberia(numSillas);
        Barbero barbero = new Barbero("Barbero 1", barb);
        barbero.start();
        
        for (int x = 1; x <= numClientes; x++) {
            new Cliente(x, barb).start();
        }
    }
    
}
