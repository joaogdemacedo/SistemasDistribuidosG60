public class Bid {

    private double bid;
    private String nick;
    private String tipoServer;

    public Bid(double b, String s, String tipo){
        this.bid=b;
        this.nick = s;
        this.tipoServer=tipo;
    }

    public String getTipoServer() {
        return tipoServer;
    }

    public void setTipoServer(String tipoServer) {
        this.tipoServer = tipoServer;
    }

    public double getBid() {
        return bid;
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
