package com.breakingbyte.game.level;

import com.breakingbyte.game.audio.AudioManager.SoundId;
import com.breakingbyte.game.engine.Debug;
import com.breakingbyte.game.engine.Engine;
import com.breakingbyte.game.render.Texture;
import com.breakingbyte.game.script.ScriptInterpreter.Script;
import com.breakingbyte.game.state.ArenaState;
import com.breakingbyte.game.util.Model;
import com.breakingbyte.game.util.SmoothJoin;
import com.breakingbyte.wrap.shared.Light;
import com.breakingbyte.wrap.shared.Renderer;
import com.breakingbyte.wrap.shared.Timer;

public class Level4 extends Level {
    
    private static final Model MESH_OBJ_ID = new Model("mdl/colony.obj");
    private static final Texture MESH_TEX_ID = new Texture("img/colony.png").useMipMap(true);
    private static final Model MESH_OBJ_LIGHTMAP_ID = new Model("mdl/colony_lm.obj").setForLightMapUse(true);
    private static final Texture MESH_TEX_LIGHTMAP_ID = new Texture("img/colony_lm.png").useMipMap(true);
        
    private static final float repeatYOffset = 106.27372f; 
    private float scrollSpeed = 13;
        
    private SmoothJoin translateJoin = new SmoothJoin();
    private SmoothJoin rotateJoin = new SmoothJoin();
    
    private float bgOffset = 0;

    public static final Level4 instance = new Level4();
    
    public Level4() {
        modelsUsed.add(MESH_OBJ_ID);
        modelsUsed.add(MESH_OBJ_LIGHTMAP_ID);
        texturesUsed.add(MESH_TEX_ID);
        texturesUsed.add(MESH_TEX_LIGHTMAP_ID);
    }
    
    @Override
    public Script getScript() {
        return ScriptLevel4.instance;
    }
    
    public SoundId getMusic() {
        return SoundId.BGM_F_T_B_R;
    }
    
    @Override
    public void initSetup() {
        
        super.initSetup(); //Load models and set-up lighting
        
        translateJoin.init(0);
        rotateJoin.init(0);
        
        shaker.shakingSpeed = 4f;
        shaker.shakingSpeed = 4f;
        shaker.xAmplitude = 0.4f; shaker.xOscill = 40f;
        shaker.yAmplitude = 0.5f; shaker.yOscill = 30f;
        
        ArenaState.instance.backgroundScrollSpeed.init(scrollSpeed);
        ArenaState.instance.backgroundScrollSpeed.setTarget(scrollSpeed, 0f);
    }
    
    @Override
    public void setUpLighting() {
        //Lighting settings
        Light.setAmbient(1f, 1f, 1f, 1f);
        Light.setDiffuse(1f, 1f, 1f, 1f);
        Light.setSpecular(1f, 1f, 1f, 1f);
        Light.setPosition(-0.3f, -0.1f, -1f, 0.0f);
        
        Light.apply();      
    }
    
    @Override
    public void update() {
        
        super.update();
        
        bgOffset += ArenaState.instance.backgroundScrollSpeed.get() * Timer.delta;
        if (bgOffset > repeatYOffset) bgOffset -= repeatYOffset;
        
        if (!Engine.player.getAutoPilot()) {
            translateJoin.setTarget(getLateralRotate()*0.4f, 5);
            rotateJoin.setTarget(getLateralRotate()*0.5f, 5);
        }
        
        translateJoin.update();
        rotateJoin.update();
    }
    
    public void render() {

        Renderer.loadPerspective();
        Renderer.DepthTest.enable();
        Renderer.Blending.disable();
        
        shaker.applyTransformation();
        
        Renderer.translate(0f, 0f, -60f);
        Renderer.rotate(-40, 1, 0, 0);
        
        Renderer.translate(0f, -bgOffset, 0);
        
        Renderer.translate(translateJoin.get(), 0, 0);
        Renderer.rotate(rotateJoin.get() * 2f, 0, 1, 0);
        
        if (Debug.useLightmap) {
            
            Renderer.enableLightMapMode(MESH_OBJ_ID.mesh, 
                    MESH_TEX_ID, 
                    MESH_OBJ_LIGHTMAP_ID.mesh, 
                    MESH_TEX_LIGHTMAP_ID);
            
            renderMesh();
            
            Renderer.disableLightMapMode();
            
        } else {
            Renderer.bindTexture(MESH_TEX_ID);  
            renderMesh();
        }
    }
    
    public void renderMesh() {
        Renderer.pushMatrix();
        Renderer.rotate(90f, 1, 0, 0);
        MESH_OBJ_ID.mesh.render();
        Renderer.popMatrix();
        
        Renderer.pushMatrix();
        Renderer.translate(0f, repeatYOffset, 0f);
        Renderer.rotate(90f, 1, 0, 0);
        MESH_OBJ_ID.mesh.renderDrawOnly();
        Renderer.popMatrix();
        
        Renderer.unbindVBOs();
    }


}
