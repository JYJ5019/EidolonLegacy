package elucent.eidolon.client.render.shader;

import elucent.eidolon.Eidolon;
import elucent.eidolon.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

final class LegacyShaderProgram {
    private final String name;
    private final ResourceLocation vertexShader;
    private final ResourceLocation fragmentShader;
    private int program;
    private int colorModulatorUniform = -1;
    private int samplerUniform = -1;
    private boolean attempted;
    private boolean failed;

    LegacyShaderProgram(String name) {
        this.name = name;
        this.vertexShader = shaderResource(name + ".vsh");
        this.fragmentShader = shaderResource(name + ".fsh");
    }

    boolean bind(float red, float green, float blue, float alpha, boolean textured) {
        if (!LegacyShaders.isSupported() || failed) {
            return false;
        }
        if (!attempted) {
            attempted = true;
            compile();
        }
        if (program == 0) {
            return false;
        }
        GL20.glUseProgram(program);
        if (colorModulatorUniform >= 0) {
            GL20.glUniform4f(colorModulatorUniform, red, green, blue, alpha);
        }
        if (textured && samplerUniform >= 0) {
            GL20.glUniform1i(samplerUniform, 0);
        }
        return true;
    }

    void delete() {
        if (program != 0) {
            GL20.glDeleteProgram(program);
            program = 0;
        }
        attempted = false;
        failed = false;
        colorModulatorUniform = -1;
        samplerUniform = -1;
    }

    private void compile() {
        int vertex = 0;
        int fragment = 0;
        try {
            vertex = compileShader(GL20.GL_VERTEX_SHADER, load(vertexShader));
            fragment = compileShader(GL20.GL_FRAGMENT_SHADER, load(fragmentShader));
            program = GL20.glCreateProgram();
            GL20.glAttachShader(program, vertex);
            GL20.glAttachShader(program, fragment);
            GL20.glLinkProgram(program);
            if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                throw new IllegalStateException(GL20.glGetProgramInfoLog(program, 4096));
            }
            colorModulatorUniform = GL20.glGetUniformLocation(program, "ColorModulator");
            samplerUniform = GL20.glGetUniformLocation(program, "Sampler0");
            Eidolon.LOGGER.info("Loaded Eidolon legacy shader {}", name);
        } catch (Exception e) {
            failed = true;
            if (program != 0) {
                GL20.glDeleteProgram(program);
                program = 0;
            }
            Eidolon.LOGGER.warn("Failed to load Eidolon legacy shader {}. Falling back to fixed pipeline.", name, e);
        } finally {
            if (vertex != 0) {
                GL20.glDeleteShader(vertex);
            }
            if (fragment != 0) {
                GL20.glDeleteShader(fragment);
            }
        }
    }

    private int compileShader(int type, String source) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shader, 4096);
            GL20.glDeleteShader(shader);
            throw new IllegalStateException(log);
        }
        return shader;
    }

    private String load(ResourceLocation location) throws IOException {
        IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(location);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static ResourceLocation shaderResource(String fileName) {
        return new ResourceLocation(Reference.MOD_ID, "shaders/legacy/" + fileName);
    }
}
