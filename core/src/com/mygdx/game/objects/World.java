package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Assets;
import com.mygdx.game.SpaceInvaders;
import com.mygdx.game.Timer;
import com.mygdx.game.screen.GameScreen;

public class World {
    Space space;
    Ship ship;
    AlienArmy alienArmy;
    BitmapFont font,fontOver,puntuacion;
    AlienShoot alienShoot;
    Alien alien;
    Timer timer = new Timer(3);

    int WORLD_WIDTH, WORLD_HEIGHT;

    public World(int WORLD_WIDTH, int WORLD_HEIGHT){
        this.WORLD_WIDTH = WORLD_WIDTH;
        this.WORLD_HEIGHT = WORLD_HEIGHT;

        space = new Space();
        ship = new Ship(WORLD_WIDTH/2);
        alienArmy = new AlienArmy(WORLD_WIDTH, WORLD_HEIGHT);
        font = new BitmapFont();
        fontOver = new BitmapFont();
        puntuacion = new BitmapFont();
    }

    public void render(float delta, SpriteBatch batch, Assets assets){

        update(delta, assets);

        batch.begin();
        space.render(batch);
        ship.render(batch);
        alienArmy.render(batch);
        font.draw(batch, "LIVE: " + ship.getVida(), 20, WORLD_HEIGHT - 5);
        puntuacion.draw(batch,"SCORE: " + ship.getPuntuacion(), 120, WORLD_HEIGHT-5);

        if(ship.getVida()==0 || ship.getVida() < 0){

            assets.shipDeath.play();
            font.draw(batch, "GAME OVER",WORLD_WIDTH/2-45, WORLD_HEIGHT/2);
            alienArmy.speedX=0;
            alienArmy.speedY=0;

            timer.update(delta);
                if (timer.check()){
                    resetGame(space,ship,alienArmy,assets,delta, batch);
                }


        }
       // batch.end();

        if(alienArmy.y <= 126){

            font.draw(batch, "GAME OVER",WORLD_WIDTH/2-45, WORLD_HEIGHT/2);
            alienArmy.speedX=0;
            alienArmy.speedY=0;

            timer.update(delta);
            if (timer.check()){
                resetGame(space,ship,alienArmy,assets,delta, batch);
            }


        }

        if (alienArmy.aliens.size == 0){

            font.draw(batch, "FELICHIDAAAAAAA",WORLD_WIDTH/2-45, WORLD_HEIGHT/2);
            alienArmy.speedX=0;
            alienArmy.speedY=0;

            timer.update(delta);
            if (timer.check()){
                resetGame(space,ship,alienArmy,assets,delta, batch);
            }


        }

       batch.end();
    }



    public void delay(SpriteBatch batch){
        try {
            font.draw(batch, "GAME OVER",WORLD_WIDTH/2-45, WORLD_HEIGHT/2);
            Thread.sleep(2000);

        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    void update(float delta, Assets assets){

        if(ship.vida>0){
        space.update(delta, assets);
        ship.update(delta, assets);
        alienArmy.update(delta, assets);

        checkCollisions(assets);
        }
    }

    private void checkCollisions(Assets assets) {
        checkNaveInWorld();
        checkShootsInWorld();
        checkShootsToAlien(assets);
        checkShootsToShip();
    }

    private void checkShootsToShip() {
        Rectangle shipRectangle = new Rectangle(ship.position.x, ship.position.y, ship.frame.getRegionWidth(), ship.frame.getRegionHeight());

        for(AlienShoot shoot: alienArmy.shoots){
            Rectangle shootRectangle = new Rectangle(shoot.position.x, shoot.position.y, shoot.frame.getRegionWidth(), shoot.frame.getRegionHeight());

            if (Intersector.overlaps(shootRectangle, shipRectangle)) {
                ship.damage();
                shoot.remove();
            }
        }
    }

    private void checkShootsToAlien(Assets assets) {
        for(Shoot shoot: ship.weapon.shoots){
            Rectangle shootRectangle = new Rectangle(shoot.position.x, shoot.position.y, shoot.frame.getRegionWidth(), shoot.frame.getRegionHeight());
            for(Alien alien: alienArmy.aliens){
                if(alien.isAlive()) {
                    Rectangle alienRectangle = new Rectangle(alien.position.x, alien.position.y, alien.frame.getRegionWidth(), alien.frame.getRegionHeight());

                    if (Intersector.overlaps(shootRectangle, alienRectangle)) {
                        alien.kill();
                        shoot.remove();
                        assets.aliendieSound.play();
                        ship.puntuacion=ship.getPuntuacion()+10;
                    }
                }
            }
        }
    }

    private void checkShootsInWorld() {
        for(Shoot shoot: ship.weapon.shoots){
            if(shoot.position.y > WORLD_HEIGHT){
                shoot.remove();
            }
        }

        for(AlienShoot shoot: alienArmy.shoots){
            if(shoot.position.y < 0){
                shoot.remove();
            }
        }
    }

    private void checkNaveInWorld() {
        if(ship.position.x > WORLD_WIDTH-32){
            ship.position.x = WORLD_WIDTH-32;
        } else if(ship.position.x < 0){
            ship.position.x = 0;
        }
    }

    public void resetGame(Space space, Ship ship, AlienArmy alienArmy, Assets assets, Float delta, SpriteBatch batch){

          ship.vida = 3;
          ship.puntuacion = 0;

        puntuacion.draw(batch,"SCORE: " + ship.getPuntuacion(), 120, WORLD_HEIGHT-5);
        this.alienArmy = new AlienArmy(WORLD_WIDTH,WORLD_HEIGHT);

        space.update(delta, assets);
        ship.update(delta, assets);
        alienArmy.update(delta, assets);

    }

}
