package primitives;

/**
 * Represents material properties of a geometry in a rendering system.
 * This class is a Plain Data Structure (PDS) and holds only simple data
 * without encapsulation logic.
 */
public class Material {

    /**
     * Transparency attenuation coefficient.
     */
    public Double3 kT = Double3.ZERO;

    /**
     * Reflection attenuation coefficient.
     */
    public Double3 kR = Double3.ZERO;

    /**
     * Ambient reflection coefficient.
     */
    public Double3 kA = Double3.ONE;

    /**
     * Diffuse reflection coefficient.
     */
    public Double3 kD = Double3.ZERO;

    /**
     * Specular reflection coefficient.
     */
    public Double3 kS = Double3.ZERO;

    /**
     * Shininess factor for specular highlight concentration.
     */
    public int nShininess = 0;

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
     *
     * @param kA ambient coefficient as a double
     * @return this Material object (for method chaining)
     */
    public Material setKA(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    /**
     * Sets the diffuse reflection coefficient using a Double3 object.
     *
     * @param kD diffuse coefficient
     * @return this Material object (for method chaining)
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Sets the diffuse reflection coefficient using a single double value.
     *
     * @param kD diffuse coefficient as a double
     * @return this Material object (for method chaining)
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Sets the specular reflection coefficient using a Double3 object.
     *
     * @param kS specular coefficient
     * @return this Material object (for method chaining)
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Sets the specular reflection coefficient using a single double value.
     *
     * @param kS specular coefficient as a double
     * @return this Material object (for method chaining)
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Sets the shininess factor.
     *
     * @param nShininess shininess exponent
     * @return this Material object (for method chaining)
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }

    /**
     * Sets the transparency attenuation coefficient.
     *
     * @param kT transparency coefficient
     * @return this Material object
     */
    public Material setKT(Double3 kT) {
        this.kT = kT;
        return this;
    }

    /**
     * Sets the transparency attenuation coefficient.
     *
     * @param kT transparency coefficient
     * @return this Material object
     */
    public Material setKT(double kT) {
        this.kT = new Double3(kT);
        return this;
    }

    /**
     * Sets the reflection attenuation coefficient.
     *
     * @param kR reflection coefficient
     * @return this Material object
     */
    public Material setKR(Double3 kR) {
        this.kR = kR;
        return this;
    }

    /**
     * Sets the reflection attenuation coefficient.
     *
     * @param kR reflection coefficient
     * @return this Material object
     */
    public Material setKR(double kR) {
        this.kR = new Double3(kR);
        return this;
    }
}