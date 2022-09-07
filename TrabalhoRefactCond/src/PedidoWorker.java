import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PedidoWorker implements Runnable {

    private Condition cond;
    private Dados dados;
    private String tipo;
    private String nick;
    private ReentrantLock lock;

    public PedidoWorker(Condition cond, Dados dados, String tipo, String nick, ReentrantLock lock) {
        this.cond = cond;
        this.dados=dados;
        this.tipo=tipo;
        this.nick=nick;
        this.lock = lock;
    }

    public void run(){
        try {
            this.lock.lock();

            this.cond.await();
            List<Server> listServ = dados.getServers().get(tipo);
            Server c = dados.servidoresLivres(listServ);
            User a =dados.getUsers().get(nick);
            a.adServer(c);
            c.setUser(nick);
            c.setEstado("ocupado");
            a.setDebt(dados.getPreco(c.getTipo()));
            a.setRelatorio("Adquiriu um servidor");

            this.lock.unlock();

        }catch(InterruptedException e){
            e.getMessage();
        }
    }

}
