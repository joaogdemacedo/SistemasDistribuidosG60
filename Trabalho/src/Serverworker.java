import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Serverworker implements Runnable {

    private Cloud cloud;
    private Socket socket;
    private User user;

    public Serverworker(Cloud clo, Socket soc) {
        this.cloud = clo;
        this.socket = soc;
        this.user=null;
    }


    public void run() {

        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            boolean menu2=false;
            while(menu2==false) {
                String res = in.readLine();
                switch (res) {
                    case "1":
                        String nick = in.readLine();
                        String pass = in.readLine();
                        while (!cloud.registerUser(nick, pass)) {
                            System.out.println("Already used");
                            out.write("Cant");
                            out.newLine();
                            out.flush();
                            nick = in.readLine();
                            pass = in.readLine();
                        }
                        System.out.println("Registered");
                        System.out.println(cloud.getUsers().get(nick).getNickname());
                        out.write("done");
                        out.newLine();
                        out.flush();
                        break;
                    case "2":
                        try {
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
                                menu2=true;
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

                        } catch (IOException e) {
                            e.getMessage();// necessário fazer os throws
                        }
                }
            }
            int autenticado = 1;
            while (autenticado==1) {
            String res=in.readLine();
            System.out.println("Estou no menu 2");

            System.out.println("opçao = " +res);
                switch (res) {
                    case "1":
                        //Estou no menu3
                        out.write(" - "+this.cloud.getPreco("large"));
                        out.newLine();
                        out.flush();
                        out.write(" - "+this.cloud.getPreco("medium"));
                        out.newLine();
                        out.flush();
                        out.write(" - "+this.cloud.getPreco("small"));
                        out.newLine();
                        out.flush();
                        res=in.readLine();
                        System.out.println("Servidor "+res);
                        Server s1=this.cloud.haDisponivel(res);
                        if(s1!=null){
                            this.user.adServer(s1);
                            this.user.setDebt(this.cloud.getPreco(res));
                            out.write("Ficou com o servidor : "+ s1.getId() + " !");
                            out.newLine();
                            out.flush();
                        }
                        else{
                            out.write("Não há servidores disponíveis!");
                            out.newLine();
                            out.flush();
                        }



                        /*
                    res=in.readLine();

                    for( Server c : this.cloud.getServers(res)) {
                        System.out.println(c.getId());
                        out.write(c.toString());
                        out.newLine();
                        out.flush();
                    }
                       out.write("done");
                       out.newLine();
                       out.flush();*/

                        break;
                    case "2":
                        System.out.println("Estou no menu das bids");
                        String op = in.readLine();
                        System.out.println("opção = " +op);
                        String tipo = null;
                        double preco;

                        if (op.equals("1")) {
                            tipo = "large";
                            preco = this.cloud.getPreco("large");
                        }
                        else if(op.equals("2")){
                            tipo = "medium";
                            preco = this.cloud.getPreco("medium");
                        }
                        else {
                            tipo = "small";
                            preco = this.cloud.getPreco("small");
                        }

                        out.write("" + preco);
                        out.newLine();
                        out.flush();

                        double bid;

                        String rec = in.readLine();
                        // faz condiçao para verificar se é letra
                        while (((bid = Double.parseDouble(rec)) >= preco)) { //se a bid der erro
                            out.write("erro");
                            out.newLine();
                            out.flush();
                        }
                        out.write("certo");
                        out.newLine();
                        out.flush();
                        this.cloud.addBid(bid, this.user.getNickname(), tipo); //insere a bid no array de bids
                        System.out.println("Licitação no valor de " + bid + "€ inserida pelo user " + this.user.getNickname());

                        break;
                    case "3":
                        String r = this.cloud.minhasBids(this.user.getNickname());
                        System.out.println(r);

                        out.write(r);
                        out.newLine();
                        out.flush();

                        String mS = this.user.meusServidores();

                        out.write(mS);
                        out.newLine();
                        out.flush();

                        break;

                    case "4":
                        String dados = this.user.getDadosPessoais();
                        out.write(dados);
                        out.newLine();
                        out.flush();
                        break;

                    case "5":
                        autenticado=0;
                        this.user.setAuthenticatedOut();
                        break;
                }
            }
            System.out.println("ola");
            this.user.setAuthenticatedOut();
            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();

        } catch (IOException e) {
            e.getMessage();
        }
    }
}
