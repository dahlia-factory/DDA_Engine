import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class DDARaycaster {

	// the window handle
	private long window;

	public void run()
	{
		init();
		loop();

		// free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();

	}

	private void init()
	{
		// set up an error callback, default imp.
		// prints error message in System.err
		GLFWErrorCallback.createPrint(System.err).set();

		// initialize GLFW
		// IMPORTANT: most GLFW shit doesn't work before you do that
		if (!glfwInit())
			throw new IllegalStateException("unable to init. glfw! fuck!");

		// configure GLFW
		glfwDefaultWindowHints();	// opional: the current window hints are already the defaults
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);			// the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);			// the window will be resizable
	
		///////////////////////
		// create the window //
		///////////////////////

		window = glfwCreateWindow(300, 300, "dda town!", NULL, NULL);

		if (window == NULL)
			throw new RuntimeException("failed to create the glfw window. fawk...");

		//////////////////////////////////////////////////////////////////////////////////////////////
		// set up a key callback. will be called every time a key is pressed, repeated, or released //
		//////////////////////////////////////////////////////////////////////////////////////////////

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			// we want to close the window when the escape key is released
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
			{
				glfwSetWindowShouldClose(window, true);	// we will detect this in the render loop
			}
		});

		// get the thread stack and push a new frame
		try (MemoryStack stack = stackPush())
		{
			IntBuffer pWidth = stack.mallocInt(1);	// int*
			IntBuffer pHeight = stack.mallocInt(1);	// int*

			// get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// get the primary monitor's res
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		}	// the stack frame is popped automatically

		// make the opengl context current
		glfwMakeContextCurrent(window);

		// enable vsync
		glfwSwapInterval(1);

		// make the window visible
		glfwShowWindow(window);
	}

	private void loop()
	{
		// this line is critical for LWJGL's interoperation with GLFW's
		// opengl context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
	
		// set the clear color
		glClearColor(0.1f, 0.0f, 0.1f, 0.0f);

		// run the rendering loop until the user has attempted to close the window
		// or pressed the escape key
		while(!glfwWindowShouldClose(window))
		{
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);		// clear the framebuffer

			glfwSwapBuffers(window);								// swap the color buffers

			// poll for window events. the key callback above will only be invoked during this call
			glfwPollEvents();
		}
	}

	public static void main(String[] args)
	{
		new DDARaycaster().run();
	}

}