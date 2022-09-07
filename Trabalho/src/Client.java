import java.io.*;
import java.net.Socket;

public class Client{

    private Socket socket;
    private String host;
    private int porto;
    private BufferedReader in ;
    private BufferedWriter out;
    private BufferedReader sysIn;
    private Cloud cloud;
    private boolean login;
    private boolean sair;

    public Client (String host, int port) throws IOException {
        this.host=host;
        this.porto=port;
        socket=new Socket (host, porto);
        in= new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        sysIn= new BufferedReader(new InputStreamReader(System.in));
        login=false;
        sair=false;
    }


    public String menu1 ()throws IOException{
        int ligado=1;
        while (ligado==1) {
            System.out.println("** ServerApp ** ");
            System.out.println("1- Registar");
            System.out.println("2- Entrar");
            System.out.println("3- Sair");

            String nick, pass, msg;
            String option = sysIn.readLine();
            out.write(option);
            out.newLine();
            out.flush();
            switch (option) {
                case "1":
                    System.out.println("Email");
                    nick = sysIn.readLine();
                    out.write(nick);
                    out.newLine();
                    out.flush();
                    System.out.println("Password");
                    pass = sysIn.readLine();
                    out.write(pass);
                    out.newLine();
                    out.flush();
                    while ((msg = in.readLine()) != null && msg.equals("Cant")) {
                        System.out.println("Utilizador já existente");

                        System.out.println("Nickname");
                        nick = sysIn.readLine();
                        out.write(nick);
                        out.newLine();
                        out.flush();

                        System.out.println("Password");
                        pass = sysIn.readLine();
                        out.write(pass);
                        out.newLine();
                        out.flush();
                    }
                    break;
                case "2": // entrar
                    try {
                        System.out.println("Email");
                        nick = sysIn.readLine();
                        out.write(nick);
                        out.newLine();
                        out.flush();
                        System.out.println("Password");
                        pass = sysIn.readLine();
                        out.write(pass);
                        out.newLine();
                        out.flush();
                        String msgs = in.readLine();
                        if (msgs.equals("Success")) {
                            //login=true;
                            //return nick;
                            menu2(nick);
                        } else System.out.println(msgs);
                        break;
                    } catch (IOException e) {
                        e.getMessage();
                    }
                case "3":
                    sair = true;
                    break;

            }
        }
        return "";

    }

    public boolean isLogin() {
        return login;
    }

    public boolean isSair() {
        return sair;
    }

    public void menu2(String nick) throws IOException{
        int logado=1;
        while (logado==1) {
            System.out.println("** Bem Vindo " + nick + "! **"); //NICK
            System.out.println("1- Comprar Servidor"); //menu3
            System.out.println("2- Licitar Servidor");
            System.out.println("3- Gerir Servidores e Bids");
            System.out.println("4- Dados pessoais");
            System.out.println("5- Sair");
            String msg;
            String option = sysIn.readLine();
            out.write(option);
            out.newLine();
            out.flush();
            switch (option) {
                case "1":
                    menu3(nick);
                    break;
                case "2":
                    menubids(nick);
                    break;
                case "3":
                    String rest = in.readLine();
                    System.out.println(rest);

                    String mS = in.readLine();
                    System.out.println(mS);

                    menu2(nick);
                    break;
                case "4":
                    System.out.println("A divída é de :" + in.readLine());
                    System.out.println("0 - Voltar ");
                    String ti = sysIn.readLine();
                    if (ti.equals("0")) {
                        menu2(nick);
                    }
                    break;
                case "5":
                    return;
                    //sair = true;
                    //break;
            }
        }


    }


    public void menubids(String nick) throws IOException{
        System.out.println("** Licitações **");
        System.out.println("1- Licitar tipo large");
        System.out.println("2- Licitar tipo medium");
        System.out.println("3- Licitar tipo small");
        System.out.println("4- Voltar");


        String option=sysIn.readLine();
        if (option.equals("4")){
            menu2(nick);
        }
        out.write(option);
        out.newLine();
        out.flush();

        String preco = in.readLine();

        System.out.println("Indique o valor que pretende licitar");
        System.out.println("Valor máximo - " + preco + "€");

        String bid=sysIn.readLine();
        out.write(bid);
        out.newLine();
        out.flush();

        String res= in.readLine();
        while (res.equals("erro")){
            System.out.println("Licitação inválida, tente outro valor.");
            String b=sysIn.readLine();
            out.write(b);
            out.newLine();
            out.flush();
            res = in.readLine();
        }
        System.out.println("Licitação efetuada com sucesso!");
        menu2(nick);

    }


    public void menu3 (String nick) throws IOException {
        System.out.println("** Servidores **");
        System.out.println("large"+ in.readLine());
        System.out.println("medium" + in.readLine());
        System.out.println("small"+ in.readLine());
        System.out.println("3- Voltar");
        System.out.println("Escreve tipo pretendido:");

        String option=sysIn.readLine();
        if (option.equals("3")){
            return;
        }

        out.write(option);
        out.newLine();
        out.flush();

        System.out.println(in.readLine());
        menu2(nick);


        /*
        String test=in.readLine();
        int i=1;
        while(!test.equals("done")) {
            System.out.println(i+ " - " + test);
            i++;
            test=in.readLine();
        }*/




        }

        public void principal() throws IOException {
            menu1();


        }



    public static void main(String [] args) throws IOException {
        Client c = new Client("127.0.0.1",12345);
        String nick = null;

        c.principal();


        while(c.isLogin()==false && c.isSair()==false) nick=c.menu1();
        if(c.isLogin()==true){
            c.menu2(nick);
        }
        else{
            System.out.println("Abraço!");
        }


    }
}
