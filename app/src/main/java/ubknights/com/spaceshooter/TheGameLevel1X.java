package ubknights.com.spaceshooter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by carlos on 10/13/2016.
 */

public class TheGameLevel1X extends Activity implements View.OnTouchListener{

    TheView mySurfaceView;      //the surfaceview, where we draw
    TheSprites2 allsprites; //class that has all the location and sizes of the images in the sprite
    float xTouch,yTouch;          //the location when the screen is touched
    int s_width,s_height;       //the size of the surfaceview
    //used for which sprite to use
    int loc = 0,eLoc=0,e1Loc=3,e2Loc=0,m1Loc=0,m2Loc=1,m3Loc=2;
    Point shipbullet,enemy1,enemy2,enemy1bullet,enemy2bullet,meteor1,meteor2,meteor3;
    long enemyMoveTime = 0;
    //15 frames per seconds
    float skipTime =1000.0f/30.0f; //setting 30fps
    long lastUpdate;
    float dt;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.spritesheetbetter); //not setting a static xml
        //set all the sprite locations and sizes
        allsprites = new TheSprites2(getResources());
        //make sure there is only ONE copy of the image and that the image
        //is in the drawable-nodpi. if it is not unwanted scaling might occur
        shipbullet = new Point(); //used for canvas drawing location
        enemy1 = new Point();     //used for canvas drawing location
        lastUpdate = 0;         //to check against now time

        //hide the actoinbar and make it fullscreen
        hideAndFull();
        //our custom view
        mySurfaceView = new TheView(this);
        mySurfaceView.setOnTouchListener(this); //now we can touch the screen
        setContentView(mySurfaceView);

       Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                //not passing an xml, using the surfaceview as the layout
                //custom way of setting the size of the surfaceview
        mySurfaceView.startGame();
            }
       },2000);


    }

    public void hideAndFull()
    {
        ActionBar bar = getActionBar();
        bar.hide();
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch(motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                xTouch = motionEvent.getX();
                yTouch = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                xTouch = motionEvent.getX();
                yTouch = motionEvent.getY();
                view.performClick();//to get rid of the message, mimicking a click
                break;
            case MotionEvent.ACTION_MOVE:
                xTouch = motionEvent.getX();
                yTouch = motionEvent.getY();
                break;
        }
        return true;
    }

    //surface view used so we can draw is dedicated made for drawing
    //View is updated in main thread while SurfaceView is updated in another thread.
    public class TheView extends SurfaceView implements SurfaceHolder.Callback {
        //resize and edit pixels in a surface. Holds the display
        SurfaceHolder holder;
        Boolean change = true;
        Thread gameThread;

        public TheView(Context context) {
            super(context);
            //get this holder
            holder = getHolder();//gets the surfaceview surface
            holder.addCallback(this);
            gameThread = new Thread(runn);
        }

        Runnable runn = new Runnable() {
            @Override
            public void run() {

                while (change == true) {
                    //perform drawing, does it have a surface?
                    if (!holder.getSurface().isValid()) {
                        continue;
                    }

                    dt = System.currentTimeMillis() - lastUpdate;
                    // Log.d("d", dt+" "+"latupdate: "+ lastUpdate);
                    if (dt >= skipTime) {
                        //look it to paint on it
                        Canvas c = holder.lockCanvas();
                        //draw the background color
                        c.drawARGB(255, 0, 0, 0);
                        //draw ship 4x the original size, you should use some percentage of the screen to make it the same size on every device
                        Rect place = new Rect((int) xTouch, s_height - allsprites.shipSize.height() * 6,
                                (int) xTouch + allsprites.shipSize.width() * 4,
                                (s_height - allsprites.shipSize.height() * 6) + allsprites.shipSize.height() * 4);
                        c.drawBitmap(allsprites.space, allsprites.shipSprites[eLoc], place, null);
                        //draw the explosion
                        place = new Rect((int) (s_width * .1f), (int) (s_height * .7f),
                                (int) (s_width * .1f) + allsprites.boomSize[loc].width() * 4,
                                (int) (s_height * .7f) + allsprites.boomSize[loc].height() * 4);
                        c.drawBitmap(allsprites.space, allsprites.boomsprites[loc], place, null);
                        //draw the enemy 1
                        place = new Rect(enemy1.x, enemy1.y, enemy1.x + allsprites.enemySize.width() * 4,
                                enemy1.y + allsprites.enemySize.height() * 4);
                        c.drawBitmap(allsprites.space, allsprites.enemy1sprites[e1Loc], place, null);
                        //draw the enemy 2
                        place = new Rect((int) (s_width * .5f), (int) (s_height * .3f),
                                (int) (s_width * .5f) + allsprites.enemySize.width() * 4,
                                (int) (s_height * .3f) + allsprites.enemySize.height() * 4);
                        c.drawBitmap(allsprites.space, allsprites.enemy2sprites[e2Loc], place, null);
                        //meteor1, using a percentage of the actual screen to draw it
                        place = new Rect((int) (s_width * .05f), (int) (s_height * .05f),
                                (int) (s_width * .05f) + (int) (s_width * .2),
                                (int) (s_height * .05f) + (int) (s_width * .2));
                        c.drawBitmap(allsprites.space, allsprites.meteorsprites[m1Loc], place, null);
                        //meteor2, using a percentage of the actual screen to draw it
                        place = new Rect((int) (s_width * .4f), (int) (s_height * .05f),
                                (int) (s_width * .4f) + (int) (s_width * .2),
                                (int) (s_height * .05f) + (int) (s_width * .2));
                        c.drawBitmap(allsprites.space, allsprites.meteorsprites[m2Loc], place, null);
                        //meteor3, using a percentage of the actual screen to draw it
                        place = new Rect((int) (s_width * .7f), (int) (s_height * .05f),
                                (int) (s_width * .7f) + (int) (s_width * .2),
                                (int) (s_height * .05f) + (int) (s_width * .2));
                        c.drawBitmap(allsprites.space, allsprites.meteorsprites[m3Loc], place, null);
                        //ship shooting

                        place = new Rect((int) shipbullet.x, shipbullet.y,
                                shipbullet.x + allsprites.shipBulletSprite.width() * 4,
                                shipbullet.y + allsprites.shipBulletSprite.height() * 4);
                        c.drawBitmap(allsprites.space, allsprites.shipBulletSprite, place, null);
                        //enemy1 shooting

                        //enemy2 shooting
                        
                        holder.unlockCanvasAndPost(c);

                        //move bullets and update sprites
                        shipbullet.y -= s_height / 20;
                        //enemy1bullet
                        //enemy2bullet
                        loc = ((loc + 1) % 6);
                        eLoc = (eLoc + 1) % 4;
                        e1Loc = (e1Loc + 1) % 6;
                        e2Loc = (e2Loc + 1) % 6;
                        m1Loc = (m1Loc + 1) % 4;
                        m2Loc = (m2Loc + 1) % 4;
                        m3Loc = (m3Loc + 1) % 4;
                        //check if bullet hit enemy or meteors
                        checkHitEnemies();

                        //check if enemies bullet hit the ship

                        //check if
                        //check if ship bullet is out of screen
                        if (shipbullet.y < allsprites.shipBulletSprite.height() * -1) {
                            resetShipBullet();
                        }
                        lastUpdate = System.currentTimeMillis();
                        //moveEnemy();
                    }
                }
            }
        };

        public void resetShipBullet()
        {
            shipbullet.y = s_height - (allsprites.shipSize.height()*4 +allsprites.shipBulletSprite.height()*4);
            shipbullet.x =   (int)xTouch +(allsprites.shipSize.width()*4/2)-allsprites.shipBulletSprite.width()*4/2;;
        }
        public void checkHitEnemies()
        {
            if(shipbullet.x > enemy1.x && shipbullet.x < enemy1.x+allsprites.enemySize.width()*4 &&
                    shipbullet.y > enemy1.y && shipbullet.y < enemy1.y+allsprites.enemySize.height()*4 )
            {
                //show explosion then spawn the enemy somewhere else and reset bullet
                //showExplosion()
                resetShipBullet();
            }
            //check bullt for enemy 2
            //else if

            //check bullet for meteor1
            //else if
            //check bullet for meteor2
            //else if
            //check bullet for meteor3
            //else if
        }
        public void moveEnemy()//set a new random location after 1 second
        {

        }
        public void startGame()
        {
            gameThread.start();
        }
        public void gameDone(){
            change = false;
            //clean the surface and show the menu by removing fullscreen
        }
        // three methods for the surfaceview
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int pixelFormat, int width, int height) {
            s_width = width;
            s_height = height;
            enemy1.x =(int)(s_width*.3f);
            enemy1.y = (int)(s_height*.3f);
            shipbullet.y = s_height - (allsprites.shipSize.height()*4 +allsprites.shipBulletSprite.height()*4);
            //add  the enemy bullets
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    }




}
