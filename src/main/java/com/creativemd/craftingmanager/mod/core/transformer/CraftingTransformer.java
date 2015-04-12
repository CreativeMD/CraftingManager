package com.creativemd.craftingmanager.mod.core.transformer;

import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.common.asm.transformers.deobf.FMLRemappingAdapter;
import net.minecraft.launchwrapper.IClassTransformer;

public class CraftingTransformer implements IClassTransformer{

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		
		String method = "func_146977_a";
		String methodob = "a";
		String desc = "(Lnet/minecraft/inventory/Slot;)V";
		String descob = "(Laay;)V";

		if(name.equals("com.creativemd.craftingmanager.mod.utils.client.GuiConfig"))
		{
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, 0);
			
			Iterator<MethodNode> methods = classNode.methods.iterator();
			while(methods.hasNext())
			{
				MethodNode m = methods.next();
				if ((m.name.equals(method) && m.desc.equals(desc)) || (m.name.equals(methodob) && m.desc.equals(descob)))
				{
					AbstractInsnNode currentNode = null;
					
					@SuppressWarnings("unchecked")
					Iterator<AbstractInsnNode> iter = m.instructions.iterator();
					
					while (iter.hasNext())
					{
						currentNode = iter.next();
						if (currentNode instanceof MethodInsnNode)
						{
							MethodInsnNode methodNode = (MethodInsnNode) currentNode;
							if((methodNode.name.equals(method) && methodNode.desc.equals(desc)) || (methodNode.name.equals(methodob) && methodNode.desc.equals(descob)))
							{
								methodNode.owner = "net/minecraft/client/gui/inventory/GuiContainer";
								if(!((MethodInsnNode)currentNode).name.equals(method))
									methodNode.owner = "bex";
								methodNode.setOpcode(INVOKESPECIAL);
							}
						}
					}
				}
			}
			
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			classNode.accept(writer);
			return writer.toByteArray();
		}
		
		if(name.equals("net.minecraft.client.gui.inventory.GuiContainer") || name.equals("bex"))
		{
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, 0);
			
			Iterator<MethodNode> methods = classNode.methods.iterator();
			while(methods.hasNext())
			{
				MethodNode m = methods.next();
				if ((m.name.equals(method) && m.desc.equals(desc)) || (m.name.equals(methodob) && m.desc.equals(descob)))
				{
					m.access = ACC_PUBLIC;
					System.out.println("[CraftingManagerCore] Patching func_146977_a ...");
				}else{
					AbstractInsnNode currentNode = null;
					
					@SuppressWarnings("unchecked")
					Iterator<AbstractInsnNode> iter = m.instructions.iterator();
					while (iter.hasNext())
					{
						currentNode = iter.next();
						if (currentNode instanceof MethodInsnNode)
						{
							MethodInsnNode methodNode = (MethodInsnNode) currentNode;
							if((methodNode.name.equals(method) && methodNode.desc.equals(desc)) || (methodNode.name.equals(methodob) && methodNode.desc.equals(descob)))
							{
								methodNode.setOpcode(INVOKEVIRTUAL);
							}
						}
					}
				}
			}
			
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			classNode.accept(writer);
			return writer.toByteArray();
		}
		
		return basicClass;
	}

}
