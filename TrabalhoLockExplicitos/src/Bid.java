public class Bid {

    private double bid;
    private String nick;
    private String tipoServer;
    private int id;


    public Bid(double b, String s, String tipo, int id){
        this.bid=b;
        this.nick = s;
        this.tipoServer=tipo;
        this.id=id;
    }

    public String getTipoServer() {
        return tipoServer;
    }

    public void setTipoServer(String tipoServer) {
        this.tipoServer = tipoServer;
    }

    public double getBidValue() {
        return bid;
    }

    public int getId() {
        return id;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public String getNick() {
        return nick;
    }



    public void setNick(String nick) {
        this.nick = nick;
    }
}
