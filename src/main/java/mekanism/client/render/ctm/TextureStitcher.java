package mekanism.client.render.ctm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TextureStitcher {

    private static List<TextureSpriteCallback> textures = new ArrayList<TextureSpriteCallback>();

	private IBakedModel baseModel;
	public static ResourceLocation baseResource = new ResourceLocation("mekanism:block/ctm_block");
	
	public String[] ctmTypes = new String[] {"dynamic_tank"};
	
    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        for (TextureSpriteCallback callback : textures) {
            callback.stitch(event.map);
        }
    }
    
    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) throws IOException {
        IModel model = event.modelLoader.getModel(baseResource);
        baseModel = model.bake(new TRSRTransformation(ModelRotation.X0_Y0), Attributes.DEFAULT_BAKED_FORMAT, r -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(r.toString()));
        
        for (String ctm : ctmTypes) {
        	ModelChisel chiselModel = new ModelChisel(ctm);
        	chiselModel.load();
            event.modelRegistry.putObject(new ModelResourceLocation("mekanism:" + ctm), new ModelChiselBlock(chiselModel));
        }
    }

    public static void register(TextureSpriteCallback callback) {
        textures.add(callback);
    }

    public static ChiselTextureCTM createTexture(String name)
    {
    	TextureSpriteCallback[] callbacks = new TextureSpriteCallback[CTM.REQUIRED_TEXTURES];
    	
    	callbacks[0] = new TextureSpriteCallback(new ResourceLocation("mekanism:textures/blocks/ctm/" + name));
    	callbacks[1] = new TextureSpriteCallback(new ResourceLocation("mekanism:textures/blocks/ctm/" + name + "-ctm"));
    	
    	register(callbacks[0]);
    	register(callbacks[1]);
    	
    	return new ChiselTextureCTM(EnumWorldBlockLayer.SOLID, callbacks);
    }
    
    public static ChiselFace createFace(String name)
    {
    	ChiselFace face = new ChiselFace();
    	
    	face.addTexture(createTexture(name));
    	face.setLayer(EnumWorldBlockLayer.SOLID);
    	
    	return face;
    }
}
