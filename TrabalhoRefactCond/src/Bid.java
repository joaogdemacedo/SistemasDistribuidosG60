import java.io.Serializable;

public class Bid implements Serializable {


    private static int idTracker=0;


    private double bid;
    private String nick;
    private String tipoServer;
    private int id;


    public double getBid() {
        return bid;
    }
    public Bid(double b, String s, String tipo){
        this.bid=b;
        this.nick = s;
        this.tipoServer = tipo;
        this.id = idTracker++;
    }

    public String getTipoServer() {
        return tipoServer;
    }

    public double getBidValue() {
        return bid;
    }

    public int getId() {
        return id;
    }

}
