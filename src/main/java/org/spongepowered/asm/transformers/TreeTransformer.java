/*
 * This file is part of Mixin, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.asm.transformers;

import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.transformer.MixinClassWriter;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * Base class for transformers which work with ASM tree model
 */
public abstract class TreeTransformer implements IClassTransformer {

    private ClassReader classReader;
    private ClassNode classNode;

    /**
     * @param basicClass Original bytecode
     * @return tree
     */
    protected final ClassNode readClass(byte[] basicClass) {
        return this.readClass(basicClass, true);
    }
    
    /**
     * @param basicClass Original bytecode
     * @param cacheReader True to cache the classReader instance for use when
     *      writing the generated ClassNode later
     * @return tree
     */
    protected final ClassNode readClass(byte[] basicClass, boolean cacheReader) {
        ClassReader classReader = new ClassReader(basicClass);
        if (cacheReader) {
            this.classReader = classReader;
        }

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
        return classNode;
    }

    /**
     * @param classNode ClassNode to write out
     * @return generated bytecode
     */
    protected final byte[] writeClass(ClassNode classNode) {
        // Use optimised writer for speed
        if (this.classReader != null && this.classNode == classNode) {
            this.classNode = null;
            ClassWriter writer = new MixinClassWriter(this.classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            this.classReader = null;
            classNode.accept(writer);
            return writer.toByteArray();
        }

        this.classNode = null;

        ClassWriter writer = new MixinClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
