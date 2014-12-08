package com.firstygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class MyGdxGame extends ApplicationAdapter {

    AssetManager assetManager;

    public PerspectiveCamera cam;
    public Model model;
    public ModelInstance instance;
    public ModelBatch modelBatch;


    public ModelInstance ship;
    public ModelInstance space;


    private int width;
    private int height;
    public Array<ModelInstance> instances = new Array<ModelInstance>();
    public Array<ModelInstance> blocks = new Array<ModelInstance>();
    public Array<ModelInstance> invaders = new Array<ModelInstance>();
    public Environment environment;




    public CameraInputController camController;

    boolean  loading=false;

    @Override
	public void create () {
        modelBatch = new ModelBatch();

        assetManager=new AssetManager();

        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        cam = new PerspectiveCamera(67, width, height);
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();


        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model);






        environment = new Environment();
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);


        assetManager.load("data/invaders.g3db", Model.class);
        loading = true;

    }

	@Override
	public void render () {

        if (loading && assetManager.update())
            doneLoading();

        camController.update();


        Gdx.gl.glViewport(0, 0, width, height);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        if (space != null)
            modelBatch.render(space);
        modelBatch.end();
	}


    private void doneLoading() {
        Model model = assetManager.get("data/invaders.g3db", Model.class);
        ship = new ModelInstance(model, "invader");
        ship.transform.setToRotation(Vector3.Y, 180).trn(0, 0, 6f).rotate(1,0,0,90);;
        instances.add(ship);

        for (float x = -5f; x <= 5f; x += 2f) {
            ModelInstance block = new ModelInstance(model, "block");
            block.transform.setToTranslation(x, 0, 3f).rotate(1,0,0,90);;
            instances.add(block);
            blocks.add(block);
        }

        for (float x = -5f; x <= 5f; x += 2f) {
            for (float z = -8f; z <= 0f; z += 2f) {
                ModelInstance invader = new ModelInstance(model, "ship");
                invader.transform.setToTranslation(x, 0, z).rotate(1,0,0,90);
                instances.add(invader);
                invaders.add(invader);
            }
        }

        space = new ModelInstance(model, "sphere");

        loading = false;
    }

    @Override
    public void dispose() {
        super.dispose();

        instances.clear();

        assetManager.dispose();
        modelBatch.dispose();
        model.dispose();

    }
}
