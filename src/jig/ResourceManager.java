package jig;

import java.net.URL;
import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;

public class ResourceManager {
	private static final HashMap<String, Image> images = new HashMap<String, Image>();
	private static final HashMap<String, Sound> sounds = new HashMap<String, Sound>();

	/**
	 * Look for a resource with a given name in logical locations
	 * using the package name of the class from which it was loaded.
	 * 
	 * @param rscName the name/pathspec of a resource to load
	 * @return a URL if the resource can be found, null otherwise
	 */
	private static URL findResource(final String rscName) {
		URL u = null;

		// Try first to get it from the actual path specified...
		u = ClassLoader.getSystemResource(rscName);

		if (u == null) {
			System.err.println("Didn't find the resource on at the location "
					+ rscName + "...preparing to hunt...");

			for (StackTraceElement ste : new Throwable().getStackTrace()) {
				if (!ste.getClassName().startsWith("jig")
						&& !ste.getClassName().startsWith("org.newdawn.slick")) {
					try {
						System.err
								.println("  - searching from location of class: "
										+ ste.getClassName());
						Class<?> callingObjClass = Class.forName(ste
								.getClassName());
						u = callingObjClass.getResource(rscName);

					} catch (ClassNotFoundException cnfe) {
						continue;
					}
					if (u != null) {
						System.err
								.println(" - Found resource '"
										+ rscName
										+ "' by inferring path. This is fragile;"
										+ " use a fully qualified path for your release.");
						return u;
					}
				}
			}
			System.err
					.println("  - Out of guesses.  Check your resource location and name. ("
							+ rscName + ")");

		}
		return u;

	}

	/**
	 * Loads an image from the hard drive given a resource name. If this is the
	 * first time this image has been loaded, it will be cached for next time so
	 * that the same image data doesn't have to be loaded every time the user
	 * calls for it.
	 * 
	 * @param rscName
	 *            The name/pathspec of the resource to load
	 * @throws SlickException
	 */
	public static void loadImage(final String rscName) {

		URL u = findResource(rscName);
		try {
			images.put(rscName, new Image(u.openStream(), rscName, false));
		} catch (Exception e) {

			System.err.println("Failed to load the resource found by the spec " + rscName);
			e.printStackTrace();
		}
	}

	/**
	 * Gets a named image resource, loading and caching it if necessary.
	 * Ideally, users should call getImage() prior to calling this method.
	 * 
	 * @param rscName
	 *            The name/pathspec of the resource to load
	 * @return An Image
	 * @throws SlickException
	 */
	public static Image getImage(final String rscName) {
		if (images.get(rscName) == null) {
			System.err.println("Warning: Image '" + rscName + "' was requested that wasn't previously loaded. Use loadImage(path) before calling getImage(path) to avoid runtime lag.");
			loadImage(rscName);
		}
		return images.get(rscName);
	}

	/**
	 * Gets a SpriteSheet from a named image resource. The named image resource
	 * will be loaded and cached if necessary. Ideally, users should call
	 * getImage() prior to calling this method.
	 * 
	 * @param rscName
	 *            The name/pathspec of the resource to load
	 * @param tx
	 *            The x width of the sprites in the sprite sheet
	 * @param ty
	 *            The y width of the sprites in the sprite sheet
	 * @return The sprite sheet data requested from the file path provided.
	 * @throws SlickException
	 */
	public static SpriteSheet getSpriteSheet(final String rscName, final int tx, final int ty) {
		if (images.get(rscName) == null) {
			System.err.println("Warning: Image '" + rscName + "' was requested that wasn't previously loaded. Use loadImage(path) before calling getImage(path) to avoid runtime lag.");
			loadImage(rscName);
		}
		return new SpriteSheet(images.get(rscName), tx, ty);
	}

	/**
	 * Removes all cached images. Every time an image is loaded, it is saved
	 * here, and if many images are loaded at one time they may start to take up
	 * quite a bit of space. Calling this should help with that.
	 */
	public static void clearImageCache() {
		images.clear();
	}

	/**
	 * Loads a sound file from the hard drive given a resource name. If this is
	 * the first time this sound has been loaded, it will be cached for next
	 * time so that the same data doesn't have to be loaded every time the user
	 * calls for it. <br>
	 * Note: Slick implements sound as two separate classes, Sound and Music. We
	 * should find a happy medium between those two.
	 * 
	 * @param rscName
	 *            The name/pathspec of the resource to load
	 * @throws SlickException
	 */
	public static void loadSound(final String rscName) {

		URL u = findResource(rscName);
		try {
			sounds.put(rscName, new Sound(u.openStream(), rscName));
		} catch (Exception e) {

			System.err.println("Failed to load the resource found by the spec " + rscName);
			e.printStackTrace();
		}
	}

	/**
	 * Gets a named sound resource, loading and caching it if necessary.
	 * Ideally, users should call getSound() prior to calling this method. <br>
	 * Note: Slick implements sound as two separate classes, Sound and Music. We
	 * should find a happy medium between those two.
	 * 
	 * @param rscName
	 *            The name/pathspec of the resource to load
	 * @return An Sound resource
	 * @throws SlickException
	 */
	public static Sound getSound(final String rscName) {
		if(sounds.get(rscName) == null) {
			System.err.println("Warning: Sound '" + rscName + "' was requested that wasn't previously loaded. Use loadSound(path) before calling getSound(path) to avoid runtime lag.");
			loadSound(rscName);
		}

		return sounds.get(rscName);
	}

	/**
	 * Removes all cached sounds. Every time an sound is loaded, it is saved here, and if many 
	 * sounds are loaded at one time they may start to take up quite a bit of space. Calling 
	 * this should help with that.
	 */
	public static void clearSoundCache() {
		sounds.clear();
	}
}
