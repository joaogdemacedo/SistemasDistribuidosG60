import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Cloud  {

    private ServerSocket socketS;
    private int porto;
    private Dados dados;


    public Cloud(int porto, Dados dados){
        this.porto=porto;
        this.dados = dados;
    }

    public Dados getDados(){
        return this.dados;
    }


    public void startCloud(){

        try{
            System.out.println("Cloud");

            this.socketS = new ServerSocket(this.porto);

            while(true) {
                Socket soc = socketS.accept();
                Serverworker sw = new Serverworker(this, soc);
                Thread th = new Thread(sw);
                th.start();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String []args){
        Dados dados = null;

        try {
            FileInputStream fileIn = new FileInputStream("Dados.txt");
            ObjectInputStream ios = new ObjectInputStream(fileIn);
            dados = (Dados) ios.readObject();
            ios.close();
            fileIn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if(dados == null) {
                Cloud cld = new Cloud(2323, new Dados());

                Server s = new Server("id1","large",50,"livre", "");
                Server s4 = new Server("id4","large",50,"livre","");

                Server s3 = new Server("id2","small",15,"livre", "");
                Server s5 = new Server("id5","small",15,"livre","");

                Server s2 = new Server("id3","medium",25,"livre", "");
                Server s6 = new Server("id6","medium",25,"livre","");

                List<Server> large = new ArrayList<>();
                List<Server> medium = new ArrayList<>();
                List<Server> small = new ArrayList<>();

                large.add(s);
                large.add(s4);

                medium.add(s2);
                medium.add(s6);

                small.add(s5);
                small.add(s3);

                cld.dados.adicionarServidores(s.getTipo(),large);

                cld.dados.adicionarServidores(s2.getTipo(),medium);
                cld.dados.adicionarServidores(s3.getTipo(),small);
                cld.startCloud();

            } else {
                Cloud cld = new Cloud(2323, dados);
                for(User c: cld.getDados().getUsers().values()){
                    c.setAuthenticatedOut();
                }
                cld.startCloud();
            }
        }
    }
}
