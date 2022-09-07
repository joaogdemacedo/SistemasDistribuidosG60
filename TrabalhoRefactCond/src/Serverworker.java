import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;

public class Serverworker implements Runnable {

    private Cloud cloud;
    private Socket socket;
    private User user;
    private int op;
    private BufferedWriter out;
    private BufferedReader in;

    public Serverworker(Cloud clo, Socket soc) throws IOException {
        this.cloud = clo;
        this.socket = soc;
        this.user=null;
        this.op=-1;
        this.in= new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

    }

    public void regista() throws IOException{
        String nick = in.readLine();
        String pass = in.readLine();
        if (!cloud.getDados().registerUser(nick, pass)) {
            out.write("Cant");
            out.newLine();
            out.flush();

        }else {
            System.out.println("Registered");
            System.out.println(cloud.getDados().getUsers().get(nick).getNickname());
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
        int error = this.cloud.getDados().testeUser(nick, pass);
        System.out.println(error);
        if (error == 3) {
            this.user = this.cloud.getDados().loginIn(nick);
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
            this.op=-2;

            while (this.op != -1) {

                String op1;
                try {

                    op1 = in.readLine();
                    this.op = Integer.parseInt(op1);

                } catch (InputMismatchException e) {
                    op = -2;
                }

                if (this.op == 0) {
                    this.op = -1;
                } else if (this.op == 1)
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

                } else if (op == 9) {
                    String relatorio = this.user.getRelatorio();
                    if (relatorio == null) {
                        out.write("Não tem notificações");
                        out.newLine();
                        out.flush();
                    } else {
                        System.out.println(relatorio);
                        out.write(relatorio);
                        out.newLine();
                        out.flush();
                        this.user.setRelatorio("Não tem notificações");
                    }

                } else if (this.op == 10) {
                    boolean s1 = this.cloud.getDados().haDisponivel("large", this.user.getNickname());
                    if (s1) {

                        out.write("Servidor  comprado com sucesso!");
                        out.newLine();
                        out.flush();
                    } else {
                        out.write("Não há servidores disponíveis e ficou em standby a compra!");
                        out.newLine();
                        out.flush();
                    }
                } else if (this.op == 11) {
                    boolean s1 = this.cloud.getDados().haDisponivel("medium", this.user.getNickname());
                    if (s1) {

                        out.write("Servidor  comprado com sucesso!");
                        out.newLine();
                        out.flush();
                    } else {
                        out.write("Não há servidores disponíveis e ficou em standby a compra!");
                        out.newLine();
                        out.flush();
                    }
                } else if (this.op == 12) {
                    boolean s1 = this.cloud.getDados().haDisponivel("small", this.user.getNickname());
                    if (s1) {

                        out.write("Servidor  comprado com sucesso!");
                        out.newLine();
                        out.flush();
                    } else {
                        out.write("Não há servidores disponíveis e ficou em standby a compra!");
                        out.newLine();
                        out.flush();
                    }
                } else if (this.op == 13) {
                    double bid;
                    double preco = this.cloud.getDados().getPreco("large");
                    out.write("" + preco);
                    out.newLine();
                    out.flush();

                    String rec = in.readLine();

                    if (((bid = Double.parseDouble(rec)) >= preco)) { //se a bid der erro
                        out.write("erro");
                        out.newLine();
                        out.flush();

                    } else {
                        if (!this.cloud.getDados().haLeilao("large", this.user.getNickname(), bid)) { // não há servidores disponiveis
                            out.write("licitado");
                            out.newLine();
                            out.flush();
                        } else {
                            out.write("comprado");
                            out.newLine();
                            out.flush();
                        }
                    }
                } else if (this.op == 14) {
                    double bid;
                    double preco = this.cloud.getDados().getPreco("medium");
                    out.write("" + preco);
                    out.newLine();
                    out.flush();

                    String rec = in.readLine();

                    if (((bid = Double.parseDouble(rec)) >= preco)) { //se a bid der erro
                        out.write("erro");
                        out.newLine();
                        out.flush();

                    } else {
                        if (!this.cloud.getDados().haLeilao("medium", this.user.getNickname(), bid)) { // não há servidores disponiveis
                            out.write("licitado");
                            out.newLine();
                            out.flush();

                        } else {
                            out.write("comprado");
                            out.newLine();
                            out.flush();
                        }
                    }
                } else if (this.op == 15) {
                    double bid;
                    double preco = this.cloud.getDados().getPreco("small");
                    out.write("" + preco);
                    out.newLine();
                    out.flush();

                    String rec = in.readLine();

                    if (((bid = Double.parseDouble(rec)) >= preco)) { //se a bid der erro
                        out.write("erro");
                        out.newLine();
                        out.flush();

                    } else {
                        if (!this.cloud.getDados().haLeilao("small", this.user.getNickname(), bid)) { // não há servidores disponiveis
                            out.write("licitado");
                            out.newLine();
                            out.flush();

                        } else {
                            out.write("comprado");
                            out.newLine();
                            out.flush();
                        }
                    }
                } else if (op == 16) {

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
                        this.cloud.getDados().libertarServidor(resA, resB);

                    }

                } else if (op == 17) {


                    String mS = this.user.asBids();
                    System.out.println(mS);
                    out.write(mS);
                    out.newLine();
                    out.flush();
                    if ( !mS.equals("Nada a apresentar.")){
                    String res = in.readLine();
                        if (res.equals("S")) {
                            int x;
                            String resB;
                            x = Integer.parseInt(in.readLine());
                            this.user.addDeb(this.user.getCBid(x));
                            this.user.removeBid(x);
                            resB = in.readLine();
                            cloud.getDados().removBid(x,resB);
                        }
                    }
                }

                try {
                    cloud.getDados().grava();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }


        }catch (IOException e){
            e.getMessage();
        }
    }

}
