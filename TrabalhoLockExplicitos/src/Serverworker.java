import java.io.*;
import java.net.Socket;

public class Serverworker implements Runnable{

    private Cloud cloud;
    private Socket socket;
    private User user;
    private int op;
    private BufferedWriter out;
    private BufferedReader in;
    private int idBids;

    public Serverworker(Cloud clo, Socket soc) throws IOException {
        this.cloud = clo;
        this.socket = soc;
        this.user=null;
        this.op=-1;
        this.in= new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.idBids=0;
    }


    /*
    public void veriA() throws IOException {
        int rst;

        if(this.user==null){
            rst = 0;
        } else {
            rst = 1;
        }

        out.write(rst);
        out.newLine();
        out.flush();
    }
    */

    public void regista() throws IOException{
        String nick = in.readLine();
        String pass = in.readLine();
        if (!cloud.registerUser(nick, pass)) {
            System.out.println("Already used");
            out.write("Cant");
            out.newLine();
            out.flush();

        }else {
            System.out.println("Registered");
            System.out.println(cloud.getUsers().get(nick).getNickname());
            out.write("done");
            out.newLine();
            out.flush();
        }
    }

    public void loginRe() throws  IOException{
        String nick,pass;
        nick = in.readLine();
        pass = in.readLine();
        System.out.println(nick);
        System.out.println(pass);
        int error = this.cloud.testeUser(nick, pass);
        System.out.println(error);
        if (error == 3) {
            this.user = this.cloud.loginIn(nick);
            out.write("Success");
            out.newLine();
            out.flush();

        } else {
            if (error == 0) {
                out.write("Utilizador não existe");
                out.newLine();
                out.flush();
            }
            if (error == 1) {
                out.write("Password errada");
                out.newLine();
                out.flush();
            }
            if (error == 2) {
                out.write("Já autenticado");
                out.newLine();
                out.flush();
            }
        }
    }
    public void run(){
        try {

            int flag=0;

            String op1 = in.readLine();
            this.op = Integer.parseInt(op1);

            while (this.op != -1) {

                if(flag==1) {
                    op1 = in.readLine();
                    this.op = Integer.parseInt(op1);
                }
                flag=1;

                if (this.op == 0) {
                    this.op = -1;
                }else if (this.op == 1)
                    this.user.setAuthenticatedOut();
                else if (this.op == 3) {
                    // Registar no Sistema
                    regista();

                } else if (this.op == 4) {
                    // Realizar Login
                    loginRe();
                } else if (op == 8) {
                    // Tratar Debito
                    String res;

                    out.write("" + this.user.getDebt());
                    out.newLine();
                    out.flush();

                    res = in.readLine();

                    if (res.equals("S")) {
                        res = in.readLine();
                        this.user.addDeb(Double.parseDouble(res));
                    }

                } else if (this.op == 9) {
                    Server s1 = this.cloud.haDisponivel("large");
                    if (s1 != null) {
                        this.user.adServer(s1);
                        this.user.setDebt(this.cloud.getPreco("large"));
                        out.write("Ficou com o servidor : " + s1.getId() + " !");
                        out.newLine();
                        out.flush();
                    } else {
                        out.write("Não há servidores disponíveis!");
                        out.newLine();
                        out.flush();
                    }
                } else if (this.op == 10) {
                    Server s1 = this.cloud.haDisponivel("medium");
                    if (s1 != null) {
                        this.user.adServer(s1);
                        this.user.setDebt(this.cloud.getPreco("medium"));
                        out.write("Ficou com o servidor : " + s1.getId() + " !");
                        out.newLine();
                        out.flush();
                    } else {
                        out.write("Não há servidores disponíveis!");
                        out.newLine();
                        out.flush();
                    }
                } else if (this.op == 11) {
                    Server s1 = this.cloud.haDisponivel("small");
                    if (s1 != null) {
                        this.user.adServer(s1);
                        this.user.setDebt(this.cloud.getPreco("small"));
                        out.write("Ficou com o servidor : " + s1.getId() + " !");
                        out.newLine();
                        out.flush();
                    } else {
                        out.write("Não há servidores disponíveis!");
                        out.newLine();
                        out.flush();
                    }
                } else if (this.op == 12) {
                    double bid;
                    double preco = this.cloud.getPreco("large");
                    out.write("" + preco);
                    out.newLine();
                    out.flush();

                    String rec = in.readLine();

                    if (((bid = Double.parseDouble(rec)) >= preco)) { //se a bid der erro
                        out.write("erro");
                        out.newLine();
                        out.flush();

                    }else {
                        out.write("certo");
                        out.newLine();
                        out.flush();


                        this.cloud.addBid(bid, this.user.getNickname(), "large", this.idBids++); //insere a bid no array de bids
                        System.out.println("Licitação no valor de " + bid + "€ inserida pelo user " + this.user.getNickname());
                    }
                } else if (this.op == 13) {
                    double bid;
                    double preco = this.cloud.getPreco("medium");
                    out.write("" + preco);
                    out.newLine();
                    out.flush();

                    String rec = in.readLine();

                    if (((bid = Double.parseDouble(rec)) >= preco)) { //se a bid der erro
                        out.write("erro");
                        out.newLine();
                        out.flush();
                    }else {
                        out.write("certo");
                        out.newLine();
                        out.flush();

                        this.cloud.addBid(bid, this.user.getNickname(), "medium", this.idBids++); //insere a bid no array de bids
                        System.out.println("Licitação no valor de " + bid + "€ inserida pelo user " + this.user.getNickname());
                    }
                } else if (this.op == 14) {
                    double bid;
                    double preco = this.cloud.getPreco("small");
                    out.write("" + preco);
                    out.newLine();
                    out.flush();

                    String rec = in.readLine();

                    if (((bid = Double.parseDouble(rec)) >= preco)) { //se a bid der erro
                        out.write("erro");
                        out.newLine();
                        out.flush();
                    } else {
                        out.write("certo");
                        out.newLine();
                        out.flush();

                        this.cloud.addBid(bid, this.user.getNickname(), "small", this.idBids++); //insere a bid no array de bids
                        System.out.println("Licitação no valor de " + bid + "€ inserida pelo user " + this.user.getNickname());
                    }
                } else if (op == 15) {
                    int idS;
                    String mS = this.user.meusServidores();

                    out.write(mS);
                    out.newLine();
                    out.flush();

                    String resA = in.readLine();
                    String resB;
                    if (resA.equals("S")) {

                        resA = in.readLine();
                        this.user.eliminarServidor(resA);

                        resB = in.readLine();
                        this.cloud.libertarServidor(resA,resB);
                    }

                } else if (op == 16) {


                    String mS = this.cloud.minhasBids(this.user.getNickname());
                    System.out.println(mS);
                    out.write(mS);
                    out.newLine();
                    out.flush();
                    if ( !mS.equals("Nada a apresentar.")){
                    String res = in.readLine();
                        if (res.equals("S")) {
                            int x;
                            x = Integer.parseInt(in.readLine());
                            this.cloud.removerBid(x);
                        }
                    }
                }

            }

            if(user!=null) {
                this.user.setAuthenticatedOut();
                System.out.println("O utilizador " + this.user.getNickname() + " desconectou-se!");
            }

        }catch (IOException e){
            e.getMessage();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
