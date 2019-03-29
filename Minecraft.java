/*
* file: Minecraft.java
* author: Henry Au, Leon Yen
* class: CS 445 – Computer Graphics
*
* assignment: Final Checkpoint
* date last modified: 11/23/2015
*
* purpose: Display multiple cubes using chunks method (creating a world at 
* least 30 cubes x 30 cubes large), with each cube textured and then randomly
* placed using simplex noise classes provided. Minimum of 6 cube types defined
* with a different texture for each one as follows: Grass, Sand, Dirt, Stone,
* Bedrock.
*/

package minecraft;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.util.glu.GLU;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.util.ResourceLoader;
import java.nio.FloatBuffer;

public class Minecraft{
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    
    private DisplayMode displayMode;
    
    public static boolean inView = false;

    /*
     * The next three methods are from the notes.
     * createWindow(), and initGL() are used to create
     * the 640x480 black display window.
     */
    private void start(){
        try{
            createWindow();
            initGL();
            gameLoop(); 
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void createWindow() throws Exception{
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();

        for(int i = 0; i < d.length; i++){
            if(d[i].getWidth() == 640
                    && d[i].getHeight() == 480
                    && d[i].getBitsPerPixel() == 32){
                displayMode = d[i];
                break;
            }
        }

        Display.setDisplayMode(displayMode);
        Display.setTitle("Fake Minecraft");
        Display.create();
    }

    private void initGL(){
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300.0f);

        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_TEXTURE_2D);
        glClearColor(0.0f,0.0f,0.0f,0.0f);

        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);

        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light's position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight); //sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight); //sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight); // sets our ambient light
        
        glEnable(GL_LIGHTING); //enable our lighting
        glEnable(GL_LIGHT0); //enables light0
    }
    
    private void initLightArrays(){
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip(); 
    }

    //moved gameLoop from Camera because OpenGL Context error
    //controls camera movement
    public void gameLoop() {
        Camera camera = new Camera(-35.0f, -35.0f, -35.0f);
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f;
        float lastTime = 0.0f;
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = 0.35f;
        Mouse.setGrabbed(true);

        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            time = Sys.getTime();
            lastTime = time;

            dx = Mouse.getDX();
            dy = Mouse.getDY();
            Frustum.init(); //initialize frustrum
            camera.yaw(dx * mouseSensitivity);
            camera.pitch(dy * mouseSensitivity);

            //Move forward
            if(Keyboard.isKeyDown(Keyboard.KEY_W)){
                camera.walkForward(movementSpeed);
            }

            //Move backwards
            if(Keyboard.isKeyDown(Keyboard.KEY_S)){
                camera.walkBackwards(movementSpeed);
            }

            //Strafe left
            if(Keyboard.isKeyDown(Keyboard.KEY_A)){
                camera.strafeLeft(movementSpeed);
            }

            //Strafe right
            if(Keyboard.isKeyDown(Keyboard.KEY_D)){
                camera.strafeRight(movementSpeed);
            }

            //Move up
            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                camera.moveUp(movementSpeed);
            }

            //Move down
            if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                camera.moveDown(movementSpeed);
            }

            glLoadIdentity();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            camera.lookThrough();
            Frustum.calculateFrustum();
            camera.chunk.render();

            Display.update();
            Display.sync(60);
        }

        Display.destroy();
    }
        
    public static void main(String[] args){
        Minecraft basic = new Minecraft();
        basic.start();
    }
}
