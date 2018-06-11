import java.io.IOException;
import java.util.Random;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;

public class Jogo extends GameCanvas implements Runnable{
    
    WildSurvival js;
    Sprite sDuck;
    Sprite sSmith;
    Sprite sRock;
    Sprite sStar;
    Sprite sMeteor2;
    Sprite sExplosion;
    Sprite sBackground;
    LayerManager lm;
    int direcaoDuck = 1;
    int direcaoSmith = 1;
    int direcaoPedra = 1;
    int desce = 0;
    int gravidade = 2;
    int velocidadeInicial = -32;
    int mru = 2;
    int velocidadeAtualY;
    int pontuacao = 0;
    int velocidadeDuck = 6;
    float desce1 = 2;
    float desce2 = 2;
    boolean morreu = false;
    int vidas = 3;
    int cont=0;
    

    
    public Jogo(WildSurvival js) {
        super(true);
        this.js = js;
        setFullScreenMode(true);
        carregaSprites();
        
        Thread gameLoop = new Thread(this);
        gameLoop.start();
    }
    
   
    public void paint(Graphics g){
        lm.paint(g, 0, 0);
        g.setColor(255, 255, 255);
        g.drawString(""+pontuacao, 10, 10, Graphics.TOP | Graphics.LEFT);
        g.drawString("Vidas: "+vidas, getWidth()-10, 10, Graphics.TOP | Graphics.RIGHT);
    }

    private void carregaSprites() {
        try {
            //Pato
            Image iDuck = Image.createImage("/jaulat.png");
            sDuck = new Sprite(iDuck, 35, 35);
            sDuck.defineReferencePixel(sDuck.getWidth()/2, (int)sDuck.getHeight()/2);
            sDuck.setPosition(100, 0);
            
            sMeteor2 = new Sprite(iDuck, 35, 35);
            sMeteor2.defineReferencePixel((int)sMeteor2.getWidth()/2,(int)sMeteor2.getHeight()/2);
            sMeteor2.setPosition(250, 0);
            
            Image iStar = Image.createImage("/bananat.png");
            sStar = new Sprite(iStar, 23, 22);
            sStar.defineReferencePixel(sStar.getWidth() /2, sStar.getHeight() /2);
            sStar.setPosition(getWidth() / 3, 0);
            
            //BACKGROUND
            Image iBackground = Image.createImage("/fundo.png");
            sBackground = new Sprite(iBackground, 240, 320);
            
            //Smith
            Image iSmith = Image.createImage("/macacot.png");
            sSmith = new Sprite(iSmith, 50, 43);
            sSmith.defineReferencePixel(sSmith.getWidth()/2, sSmith.getHeight()/2);
            sSmith.setPosition(getWidth()/2, getHeight()- 30 - sSmith.getHeight());
            
            //Pedra
            Image iRock = Image.createImage("/rock.png");
            sRock = new Sprite(iRock);
            sRock.defineReferencePixel(sRock.getWidth()/2, sRock.getHeight()/2);
            sRock.setVisible(false);
            
            //Explosao
            Image iExplosion = Image.createImage("/explosion.png");
            sExplosion = new Sprite(iExplosion, 75, 75);
            sExplosion.defineReferencePixel(sExplosion.getWidth()/2, sExplosion.getHeight()/2);
            sExplosion.setVisible(false);
            
            lm = new LayerManager();
            lm.append(sSmith);
            lm.append(sStar);
            lm.append(sDuck); 
            lm.append(sMeteor2);
            lm.append(sRock);
            lm.append(sExplosion);
            lm.append(sBackground);
            
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

   
    public void run() {      
        while(!morreu){
            verificaJogador();
            atualizaJogo();
            verificaColisao();
            repaint();
            try {
                Thread.sleep(30);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        js.gameOver(pontuacao);
    }

    private void atualizaJogo() {
        
        sDuck.move(0, (int)desce1);  
        sStar.move(0, 6);
        if(pontuacao <0){
            pontuacao = 0;
        }
        if(pontuacao > 100){
            sMeteor2.setVisible(true);
            sMeteor2.move(0, (int)desce2);
        }
        
        if(sDuck.getY() > getHeight()){
            posicao(sDuck);
            if(pontuacao>0)
               pontuacao -= 10;

        }
        
        if(sMeteor2.getY() > getHeight()){
            posicao(sMeteor2);
            if(pontuacao>0)
               pontuacao -= 10;
        }
        
        if(sStar.getY() > getHeight())
            posicao(sStar);
            
        if(sRock.isVisible()){
            sRock.move(0, -25);
            if(sRock.getY() < 0 - sRock.getHeight())
                sRock.setVisible(false);
        }
        
        if(sExplosion.isVisible()){
            sExplosion.nextFrame();
            if(sExplosion.getFrame() == sExplosion.getRawFrameCount()-1) {
                sExplosion.setVisible(false);
                pontuacao += 10;

            }
        }
    }

  private void verificaJogador() {
        int state = getKeyStates();
        if ((state & RIGHT_PRESSED) > 0) {
            if (sSmith.getX() + sSmith.getWidth() < getWidth()) {
                direcaoSmith = 1;
                sSmith.setTransform(Sprite.TRANS_MIRROR);
                sSmith.nextFrame();
                sSmith.move(3, 0);
            }
        } else if ((state & LEFT_PRESSED) > 0) {
            if (sSmith.getX() >  0) {
                direcaoSmith = -1;
                sSmith.setTransform(Sprite.TRANS_NONE);
                sSmith.nextFrame();
                sSmith.move(-3, 0);
            }
        } else if ((state & FIRE_PRESSED) > 0) {
            if (!sRock.isVisible()) {
                sRock.setRefPixelPosition(sSmith.getRefPixelX(), sSmith.getRefPixelY());
                sRock.setVisible(true);
                velocidadeAtualY = velocidadeInicial;
                direcaoPedra = direcaoSmith;
            }
        }
    }

    private void verificaColisao() {   
        
        if(sDuck.collidesWith(sSmith, true) || sMeteor2.collidesWith(sSmith, true) ){
            if(vidas == 0){
                morreu = true;
            }else{
                vidas--;
                sSmith.setPosition(getWidth()/2, getHeight()- 30 - sSmith.getHeight() );
                posicao(sDuck);
                posicao(sMeteor2);
            }
        }
        
        
        if(sDuck.collidesWith(sRock, true)){
            sExplosion.setRefPixelPosition(sDuck.getRefPixelX(), sDuck.getRefPixelY());
            sExplosion.setVisible(true);
            sRock.setVisible(false);
            posicao(sDuck);
            pontuacao += 10;
        }
        
        if(sMeteor2.collidesWith(sRock, true)){
            sExplosion.setRefPixelPosition(sMeteor2.getRefPixelX(), sMeteor2.getRefPixelY());
            sExplosion.setVisible(true);
            sRock.setVisible(false);
            posicao(sMeteor2);
            pontuacao += 10;
        }
        
        if(sMeteor2.collidesWith(sDuck, true)){
            posicao(sMeteor2);            
        }
        
        if(sStar.collidesWith(sSmith, true)){
            posicao(sStar);
            pontuacao += 15;
            cont ++;
            if (cont == 10){
                vidas ++;
                cont = 0;
            }
        }
    }

    private void posicao(Sprite Meteor) {
        Random m = new Random();
        int X = m.nextInt(getWidth() - Meteor.getWidth());
        int Y = m.nextInt(getHeight());
        Y *= -1;
        Meteor.setPosition(X, Y);
        if (Y < 80)
            desce1 += 0.1;
        if (Y < 70)
            desce2 += 0.1;
    }
}
