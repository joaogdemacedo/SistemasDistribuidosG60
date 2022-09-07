import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client  {

    private Socket socket;
    private String host;
    private int porto;
    private BufferedReader in ;
    private BufferedWriter out;
    private BufferedReader sysIn;
    private int op;


    public Client (String host, int port) throws IOException {
        this.host=host;
        this.porto=port;
        socket=new Socket(host, porto);
        in= new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        sysIn= new BufferedReader(new InputStreamReader(System.in));
        this.op=-1;
    }

    public void showMenu() {
        System.out.println("\n*  --------------------  *");
        System.out.println("*     SERVER_BUY APP     *");
        System.out.println("*  --------------------  *");
        System.out.println("3 ---    Registar    --- *");
        System.out.println("4 ---     Entrar     --- *");
        System.out.println("0 ----     Sair     ---- *\n");
    }

    public void menuPrincipal() {
        System.out.println("\n****       MENU PRINCIPAL      ****");
        System.out.println("-----------------------------------");
        System.out.println("5 ---     Comprar Servidor    --- *");
        System.out.println("6 ---     Licitar Servidor    --- *");
        System.out.println("7 --- Gerir Servidores e Bids --- *");
        System.out.println("8 ---     Dados Pessoais      --- *");
        System.out.println("9 ---      Notificações       --- *");
        System.out.println("1 ----        Logout         ---- *\n");
    }

    public void menuComprar() {
        System.out.println("\n****     MENU COMPRAR    ****");
        System.out.println("-----------------------------");
        System.out.println("10  ---   Large  50€/h   --- *");
        System.out.println("11 ---   Medium 25€/h   --- *");
        System.out.println("12 ---   Small  15€/h   --- *");
        System.out.println("2  ----     Voltar     ----\n");
    }

    public void menuBids() {
        System.out.println("\n****       MENU LICITAÇÕES      ****");
        System.out.println("------------------------------------");
        System.out.println("13 --- Licitar Servidor Large  --- *");
        System.out.println("14 --- Licitar Servidor Medium --- *");
        System.out.println("15 --- Licitar Servidor Small  --- *");
        System.out.println("2  ----        Voltar         ---- *\n");
    }

    public void menuGerir(){
        System.out.println("\n****     MENU GERIR      ****");
        System.out.println("-----------------------------");
        System.out.println("16 ---  Ver Servidores  --- *");
        System.out.println("17 ---  Ver Licitações  --- *");
        System.out.println("2  ----     Voltar     ---- *\n");
    }

    public void escolherOpcao(int outro, int limitInf,int limiteSup) throws IOException {
        int op;
        Scanner input = new Scanner(System.in);

        System.out.print("Escolha a opcao: ");

        try {
            op = input.nextInt();
            while ((op<limitInf || op>limiteSup) && op!=outro) { //
                System.out.println("Erro! Opção invalida, tenta de novo.");
                op = input.nextInt();
            }
        } catch (InputMismatchException e) {
            op = -3;
        }

        this.op = op;

        out.write(Integer.toString(op));
        out.newLine();
        out.flush();
    }

    public void registar() throws IOException{
        String nick, pass, msg;

        System.out.print("Nickname: ");
        nick= sysIn.readLine();
        out.write(nick);
        out.newLine();
        out.flush();
        System.out.print("Password: ");
        pass=sysIn.readLine();
        out.write(pass);
        out.newLine();
        out.flush();
        if((msg=in.readLine()) !=null && msg.equals("Cant")){
            System.out.println("Utilizador já existente.");
        }else{
            System.out.println("Registado com sucesso!");
        }
    }

    public int login() throws  IOException{
        String nick, pass;
        int rtn;

        System.out.print("Nickname: ");
        nick = sysIn.readLine();
        out.write(nick);
        out.newLine();
        out.flush();
        System.out.print("Password: ");
        pass = sysIn.readLine();
        out.write(pass);
        out.newLine();
        out.flush();
        String msgs = in.readLine();

        if(msgs.equals("Success")) {
            rtn = 1;
        } else {
            rtn = 0;
            System.out.println(msgs);
        }

        return rtn;
    }

    public void bids() throws IOException{
        String preco=in.readLine();
        System.out.println("Indique o valor que pretende licitar.");
        System.out.println("Valor máximo - " + preco + "€");

        String bid=sysIn.readLine();
        out.write(bid);
        out.newLine();
        out.flush();

        String res= in.readLine();
        if (res.equals("erro")){
            System.out.println("Licitação inválida, tente outro valor.");
        }
        else if (res.equals("licitado"))
            System.out.println("Licitação efetuada com sucesso!");
        else {
            System.out.println("Servidor comprado com sucesso!");
        }

    }

    public void trataGerir() throws IOException{

        String servidores=in.readLine();
        String msgg;


        if(servidores.equals("Nada a apresentar.")){
            System.out.println(servidores);
        }else {
            System.out.println(servidores);
            System.out.println("Deseja cancelar alguma opção? S/N");
            msgg=sysIn.readLine();
            out.write(msgg);
            out.newLine();
            out.flush();

            if( msgg.equals("S")) {

                System.out.println("Indique o id :");
                msgg = sysIn.readLine();
                out.write(msgg);
                out.newLine();
                out.flush();

                System.out.println("Indique o seu tipo:");
                msgg = sysIn.readLine();
                out.write(msgg);
                out.newLine();
                out.flush();

            }
        }
    }

    public void trataDebt() throws IOException{
        String msgg;
        System.out.println("A divída é de : " + in.readLine() + "€");
        System.out.println("Quer abater a divída? S/N");
        msgg=sysIn.readLine();
        out.write(msgg);
        out.newLine();
        out.flush();
        if( msgg.equals("S")){
            System.out.println("Quanto deseja depositar: ");
            msgg=sysIn.readLine();
            out.write(msgg);
            out.newLine();
            out.flush();
        }
    }

    public void trataNotificacoes() throws IOException{
        String relatorio = in.readLine();
        System.out.println(relatorio);
        System.out.println("2  ----     Voltar     ---- *\n");
    }

    public void correr() throws IOException{
        showMenu();
        escolherOpcao(0,3,4);


        while(this.op!=-1){

            if(this.op==0){
                this.op=-1;
            }
            else if(this.op==1 || this.op==-3){
                // Voltar para o Menu1
                showMenu();
                escolherOpcao(0,3,4);
            }
            else if(this.op==2){
                // Voltar para o Menu 2
                menuPrincipal();
                escolherOpcao(1,5,9);
            }
            else if(this.op==3){
                // Registar no Sistema
                registar();
                showMenu();
                escolherOpcao(0,3,4);
            }
            else if(this.op==4){
                // Realizar Login
                if(login()==1){
                    menuPrincipal();
                    escolherOpcao(1,5,9);
                } else {
                    showMenu();
                    escolherOpcao(0,3,4);
                }
            }
            else if(this.op==5){
                //Comprar Servidor

                menuComprar();
                escolherOpcao(2,10,12);
            }
            else if(this.op==6){
                // Menu de licitações
                menuBids();
                escolherOpcao(2, 13,15);
            }
            else if(op==7){
                menuGerir();
                escolherOpcao(2,16,17);
            }
            else if(op==8){
                trataDebt();
                menuPrincipal();
                escolherOpcao(1,5,9);
            }
            else if(op==9){
                trataNotificacoes();
                String acao=sysIn.readLine();
                while(!acao.equals("2")){
                    System.out.println("Erro! Opção invalida, tenta de novo.");
                    acao=sysIn.readLine();
                }
                menuPrincipal();
                escolherOpcao(1,5,9);
            }
            else if(this.op==10 || this.op==11 || this.op==12 ) {
                // Comprar Large
                // Alterar no lado do ServerWorker
                String msgIN = in.readLine();
                System.out.println(msgIN);
                menuPrincipal();
                escolherOpcao(1,5,9);
            }

            else if(this.op==13 || this.op==14 || this.op==15 ){
                bids();
                menuBids();
                escolherOpcao(2,13,15);
            }
            else if (op==16 || op==17) {
                trataGerir();
                menuPrincipal();
                escolherOpcao(1, 5, 9);
            }


        }
        System.out.println("Até Breve!");

    }

    public static void main(String args[]) throws  IOException{

        Client c = new Client("localhost",2323);
        c.correr();
    }








}

