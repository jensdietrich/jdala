package nz.ac.wgtn.ecs.jdala.visitors;

import nz.ac.wgtn.ecs.jdala.utils.AnnotationPair;
import nz.ac.wgtn.ecs.jdala.utils.PortalClass;
import nz.ac.wgtn.ecs.jdala.utils.PortalMethod;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Class visitor for transformation, the main function is to calls {@link TransformerMethodVisitor} in the method visitor
 * and passes in the classname, annotations, and the superClassName.
 *
 * @author Quinten Smit
 */
public class TransformerClassVisitor extends ClassVisitor {
    private final String className;
    final Set<AnnotationPair> annotations;
    private String superClassName;
    private final Set<PortalClass> portalClasses;
    private PortalClass implementedPortalClass = null;

    public TransformerClassVisitor(int api, ClassVisitor classVisitor, Set<AnnotationPair> annotations, String className, Set<PortalClass> portalClasses) {
        super(api, classVisitor);
        this.className = className;
        this.annotations = annotations;
        this.portalClasses = portalClasses;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.superClassName = superName;

        for (PortalClass portalClass : portalClasses) {
            String targetClazz = portalClass.getClassName();
            String targetInternalName = targetClazz.replace('.', '/');
            if (name.equals(targetInternalName)){
                System.out.println(name + " is " + targetInternalName);
                if (implementedPortalClass != null) throw new IllegalStateException("Invalid JSON: " + targetInternalName + " is registered twice in portal class file " + portalClass + " " + implementedPortalClass);
                implementedPortalClass = portalClass;
            }
            if (!portalClass.includesSubClasses()) continue;

            if (checkInheritanceOrImplementation(superName, interfaces, targetInternalName)) {
                System.out.println(name + " extends/implements " + targetInternalName);
                if (implementedPortalClass != null) throw new IllegalStateException("Invalid JSON: " + targetInternalName + " is registered twice in portal class file " + portalClass + " " + implementedPortalClass);
                implementedPortalClass = portalClass;
            }
        }

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        String methodPath = className.replace('/', '.');

        PortalMethod portalMethod = null;

        if (implementedPortalClass != null){
            portalMethod = implementedPortalClass.getPortalMethod(name, descriptor);
        }

        return new TransformerMethodVisitor(mv, superClassName, annotations, methodPath, name, portalMethod);
    }

    private boolean checkInheritanceOrImplementation(String superName, String[] interfaces, String targetInternalName) {
        // Direct interface implementation
        for (String interfaceName : interfaces) {
            if (interfaceName.equals(targetInternalName)) {
                return true;
            }

            // Recursively check parent interfaces
            if (checkInheritanceOrImplementation(interfaceName, targetInternalName)) {
                return true;
            }
        }

        // Direct superclass check
        if (superName != null) {
            if (superName.equals(targetInternalName)) {
                return true;
            }

            // Recursively check the superclass chain
            return checkInheritanceOrImplementation(superName, targetInternalName);
        }

        return false;
    }

    /**
     * Check if the current class implements or extends the targetClassName.
     * Recursively checks up the class chain
     * @param className The name of the currently visiting class
     * @param targetClassName Name of target
     * @return if the class extends or implements the targetClassName
     */
    private boolean checkInheritanceOrImplementation(String className, String targetClassName) {
        try (InputStream in = getClassInputStream(className)) {
            if (in == null) {
                return false;
            }

            ClassReader cr = new ClassReader(in);

            final boolean[] found = {false};

            cr.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    if (name.equals(targetClassName)) {
                        found[0] = true;
                        return;
                    }

                    found[0] = checkInheritanceOrImplementation(superName, interfaces, targetClassName);
                }
            }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

            return found[0];

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load classes so that they can be used to view inheritance
     * @param internalName The name of the class to load
     * @return The class file
     */
    private InputStream getClassInputStream(String internalName) {
        String resource = internalName + ".class";
        ClassLoader cl = getClass().getClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        return cl.getResourceAsStream(resource);
    }

}