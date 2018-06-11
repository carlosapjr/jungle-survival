import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStore;

public class WildSurvival extends MIDlet implements CommandListener, Runnable{
    
    Jogo jogo;
    Display display = Display.getDisplay(this);
    Command cmdOk = new Command("OK", Command.OK, 1);
    Command cmdMenu = new Command("Menu", Command.OK, 1);    
    TextField tfNome;
    int ultimoRecord = 0;
    int ultimaPontuacao = 0;    
    Form fTelaRecords;
    
    List menu = new List("Wild Survival", List.IMPLICIT);
    
    String acao;

    public void startApp() {
        if (menu.size() == 0) {
            menu.append("Novo Jogo", null);
            menu.append("Record", null);
            menu.append("Ajuda", null);
            menu.append("Sobre", null);
            menu.append("Sair", null);
            menu.setCommandListener(this);
        }
        display.setCurrent(menu);
    }
    
    public void pauseApp() {  }
    
    public void destroyApp(boolean unconditional) {    }
    
    public void gameOver(int pontuacao) {
        int record = verificaRecord(pontuacao);
        ultimoRecord = record;
        ultimaPontuacao = pontuacao;
        acao = "AddRecord";
        Form f = new Form("Wild Survival");
        f.append("Sua pontuação foi " + pontuacao + " pontos");
        f.setCommandListener(this);
        f.addCommand(cmdOk);
        display.setCurrent(f);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == List.SELECT_COMMAND){
            int op = menu.getSelectedIndex();
            switch (op) {
                case 0: novoJogo();
                        break;
                case 1: recordes();
                        break;
                case 2: ajuda();
                        break;
                case 3: sobre();
                        break;
                case 4: notifyDestroyed();
            }
            
        } else if (c == cmdOk) {
            display.setCurrent(menu);
         } else if (c == cmdMenu) {
            display.setCurrent(menu);
        }
    }

    private int verificaRecord(int pontuacao) {
        int result = 0;
        try {
            RecordStore rs = RecordStore.openRecordStore("pontuação", true);
            String pontos = ""+pontuacao;
            byte[] dados = pontos.getBytes();
            if (rs.getNumRecords() == 0) {
                rs.addRecord(dados, 0, dados.length);
                result = pontuacao;
            } else {
                byte[] recordAtual = rs.getRecord(1);
                String sRecordAtual = new String(recordAtual);
                int iRecordAtual = Integer.parseInt(sRecordAtual);
                if (pontuacao > iRecordAtual) {
                    rs.setRecord(1, dados, 0, dados.length);
                    result = pontuacao;
                } else {
                    result = iRecordAtual;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }       
        
        return result;
    }

    public void run() {   }
    
    public Image getScaledImage(Image original, int newWidth, int newHeight) {

        int wOriginal = original.getWidth();
        int hOriginal = original.getHeight();
        int[] rawInput = new int[hOriginal * wOriginal];
        original.getRGB(rawInput, 0, wOriginal, 0, 0, wOriginal, hOriginal);

        int[] rawOutput = new int[newWidth * newHeight];

        int YD = (hOriginal / newHeight) * wOriginal - wOriginal;
        int YR = hOriginal % newHeight;
        int XD = wOriginal / newWidth;
        int XR = wOriginal % newWidth;
        int outOffset = 0;
        int inOffset = 0;

        for (int y = newHeight, YE = 0; y > 0; y--) {
            for (int x = newWidth, XE = 0; x > 0; x--) {
                rawOutput[outOffset++] = rawInput[inOffset];
                inOffset += XD;
                XE += XR;
                if (XE >= newWidth) {
                    XE -= newWidth;
                    inOffset++;
                }
            }
            inOffset += YD;
            YE += YR;
           if (YE >= newHeight) {
                YE -= newHeight;
                inOffset += wOriginal;
            }
        }
        return Image.createRGBImage(rawOutput, newWidth, newHeight, true);
    }

    private void novoJogo() {
        jogo = new Jogo(this);
        display.setCurrent(jogo);
        final WildSurvival dh = this;
        new Thread() {
            public void run(){}
        }.start();                    
    }

    private void recordes() {
        Form f = new Form("Record");
        ultimoRecord = verificaRecord(0);
        acao = "ListRecord";
        f.append(String.valueOf(ultimoRecord));
        f.addCommand(cmdMenu);
        f.setCommandListener(this);
        display.setCurrent(f);
        
    }

    private void ajuda() {
        Form f = new Form("Ajuda");
        f.append("Wild Survival é um jogo divertido no qual você deve ajudar o guloso macaquinho Estevam a conseguir pegar todas as bananas que estão caindo e desviar das armadilhas. "+
                "Para isso use as teclas '4' e '6' para se movimentar e 'espaço' para jogar pedras.");
        f.addCommand(cmdMenu);
        f.setCommandListener(this);
        display.setCurrent(f);
    }

    private void sobre() {
        Form f = new Form("Sobre");
        f.append("Wild Survival versão 1.0 desenvolvido por "+
                "Carlos Alberto - contato: carlos.cj.alberto@gmail.com");
        f.addCommand(cmdMenu);
        f.setCommandListener(this);
        display.setCurrent(f);        
    }
}
