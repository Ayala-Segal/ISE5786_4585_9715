package primitives;

/**
 * Represents material properties of a geometry in a rendering system.
 * This class is a Plain Data Structure (PDS) and holds only simple data
 * without encapsulation logic.
 */
public class Material {

    /**
     * Ambient reflection coefficient.
     */
    public Double3 kA = Double3.ONE;

    /**
     * Default constructor.
     * Initializes material with default values.
     */
    public Material() {
    }

    /**
     * Sets the ambient reflection coefficient using a Double3 object.
     *
     * @param kA ambient coefficient
     * @return this Material object (for method chaining)
     */
    public Material setKA(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Sets the ambient reflection coefficient using a single double value.
     * Converts the value into a Double3 object internally.
     *
     * @param kA ambient coefficient as a double
     * @return this Material object (for method chaining)
     */
    public Material setKA(double kA) {
        this.kA = new Double3(kA);
        return this;
    }
}