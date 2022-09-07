
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Leilaoworker implements Runnable {

    private Dados dados;
    private String nick;
    private String tipo;
    private int idbid;
    private double bid;
    private Condition noServers;
    private ReentrantLock lock;

    public Leilaoworker(Dados dados, String nick, String tipo, int idbid, Condition cond, ReentrantLock lock, double bid) {
        this.dados = dados;
        this.nick = nick;
        this.tipo = tipo;
        this.idbid = idbid;
        this.bid=bid;
        this.lock = lock;
        this.noServers = cond;
    }

    public void run(){

        try {
            this.lock.lock();

            this.noServers.await();
            List<Server> listServ = dados.getServers().get(tipo);
            Server c = dados.servidoresLivres(listServ);
            if (c != null) {
                User a = dados.getUsers().get(nick);
                a.adServer(c);
                a.removeBid(this.idbid);
                c.setUser(nick);
                c.setEstado("leilao");
                a.setDebt(bid);
                a.setRelatorio("Adquiriu um servidor");
            }
            this.lock.unlock();
        }catch(InterruptedException e){
                e.getMessage();
            }


    }
}
