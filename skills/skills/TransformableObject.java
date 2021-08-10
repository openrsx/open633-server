package skills;

import lombok.Getter;

/**
 * Acts as a transformable object which switches between states.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class TransformableObject {
	
	/**
	 * The original state of this object.
	 */
	@Getter
	private final int objectId;
	
	/**
	 * The transformable states this object can transform into.
	 */
	@Getter
	private final int transformable;
	
	/**
	 * Constructs a new {@link TransformableObject}.
	 * @param objectId {@link #objectId}.
	 * @param transformable {@link #transformable}.
	 */
	public TransformableObject(int objectId, int transformable) {
		this.objectId = objectId;
		this.transformable = transformable;
	}
}
