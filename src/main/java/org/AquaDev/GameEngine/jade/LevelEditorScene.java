package org.AquaDev.GameEngine.jade;

import org.AquaDev.GameEngine.renderer.Shader;
import org.AquaDev.GameEngine.renderer.Texture;
import org.AquaDev.GameEngine.util.Time;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene {

    private float[] vertexArray = {
             // position                    // color                  // UV Co-ordinates
             100f, 0f, 0.0f,                1.0f, 0.0f, 0.0f, 1.0f,   1, 1, // Bottom right
             0f,  100f, 0.0f,               0.0f, 1.0f, 0.0f, 1.0f,   0, 0, // Top left
             100f,  100f, 0.0f ,            0.0f, 0.0f, 1.0f, 1.0f,   1, 0, // Top right
             0f, 0f, 0.0f,                  1.0f, 1.0f, 0.0f, 1.0f,   0, 1, // Bottom Left
    };

    private int[] elementArray = {
            /*
                    x       x


                    x       x
             */
            2, 1, 0, // Top right triangle
            0, 1, 3, // Bottom left Triangle
    };

    private int vaoID, vboID, eboID;

    private Shader defaultShader;
    private Texture testTexture;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());
        defaultShader= new Shader("assets/shaders/default.glsl");
        defaultShader.compile();
        this.testTexture = new Texture("assets/images/testImage.png");

        // Generate VAO, VBO, and EBO buffer objects, and send to GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO and upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);

        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indecies and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colorsSize = 4;
        int uvSize = 2;
        int vertxSizeBytes = (positionsSize + colorsSize + uvSize) * Float.BYTES;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertxSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorsSize, GL_FLOAT, false, vertxSizeBytes, positionsSize * Float.BYTES);

        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertxSizeBytes, (positionsSize + colorsSize) * Float.BYTES);
        glEnableVertexAttribArray(2);


    }

    @Override
    public void update(float dt) {


        defaultShader.use();
        // Upload Texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        // Bind what VAO we are using
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        defaultShader.detach();
    }
}
