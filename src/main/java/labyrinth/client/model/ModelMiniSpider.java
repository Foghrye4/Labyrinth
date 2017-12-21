package labyrinth.client.model;

import labyrinth.LabyrinthMod;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ModelMiniSpider extends ModelBase {
	private final ResourceLocation leg = new ResourceLocation(LabyrinthMod.MODID, "models/entity/mini_spider_leg.obj");
	private final ResourceLocation body = new ResourceLocation(LabyrinthMod.MODID, "models/entity/mini_spider_body.obj");
	private final ResourceLocation test = new ResourceLocation(LabyrinthMod.MODID, "models/entity/cube.obj");

	public ObjModelRenderer testMesh;
	
    public ObjModelRenderer spiderBody;
    /** Spider's first leg */
    public ObjModelRenderer spiderLeg1;
    /** Spider's second leg */
    public ObjModelRenderer spiderLeg2;
    /** Spider's third leg */
    public ObjModelRenderer spiderLeg3;
    /** Spider's fourth leg */
    public ObjModelRenderer spiderLeg4;
    /** Spider's fifth leg */
    public ObjModelRenderer spiderLeg5;
    /** Spider's sixth leg */
    public ObjModelRenderer spiderLeg6;
    /** Spider's seventh leg */
    public ObjModelRenderer spiderLeg7;
    /** Spider's eight leg */
    public ObjModelRenderer spiderLeg8;

    public ModelMiniSpider()
    {
    	this.testMesh = new ObjModelRenderer(this);
        this.testMesh.addObjModel(test, -8.0F, -4.0F, 0.0F, 16, 16, 16, 0.0F);
        this.testMesh.setRotationPoint(0.0F, 15.0F, 9.0F);
        
        this.spiderBody = new ObjModelRenderer(this);
        this.spiderBody.addObjModel(body, -8.0F, -4.0F, 0.0F, 16, 16, 16, 0.0F);
        this.spiderBody.setRotationPoint(0.0F, 15.0F, 9.0F);
        
        // Left legs
        this.spiderLeg2 = new ObjModelRenderer(this);
        this.spiderLeg2.addObjModel(leg, -1.0F, -1.0F, -1.0F, 16, 16, 16, 0.0F);
        this.spiderLeg2.setRotationPoint(4.0F, 15.0F, 2.0F);
        this.spiderLeg4 = new ObjModelRenderer(this);
        this.spiderLeg4.addObjModel(leg, -1.0F, -1.0F, -1.0F, 16, 16, 16, 0.0F);
        this.spiderLeg4.setRotationPoint(4.0F, 15.0F, 1.0F);
        this.spiderLeg6 = new ObjModelRenderer(this);
        this.spiderLeg6.addObjModel(leg, -1.0F, -1.0F, -1.0F, 16, 16, 16, 0.0F);
        this.spiderLeg6.setRotationPoint(4.0F, 15.0F, 0.0F);
        this.spiderLeg8 = new ObjModelRenderer(this);
        this.spiderLeg8.addObjModel(leg, -1.0F, -1.0F, -1.0F, 16, 16, 16, 0.0F);
        this.spiderLeg8.setRotationPoint(4.0F, 15.0F, -1.0F);
        
        // Right legs
        this.spiderLeg1 = new ObjModelRenderer(this);
        this.spiderLeg1.addObjModel(leg, -15.0F, -1.0F, -1.0F, 16, 16, 16, 0.0F);
        this.spiderLeg1.setRotationPoint(-4.0F, 15.0F, 2.0F);
        this.spiderLeg3 = new ObjModelRenderer(this);
        this.spiderLeg3.addObjModel(leg, -15.0F, -1.0F, -1.0F, 16, 16, 16, 0.0F);
        this.spiderLeg3.setRotationPoint(-4.0F, 15.0F, 1.0F);
        this.spiderLeg5 = new ObjModelRenderer(this);
        this.spiderLeg5.addObjModel(leg, -15.0F, -1.0F, -1.0F, 16, 16, 16, 0.0F);
        this.spiderLeg5.setRotationPoint(-4.0F, 15.0F, 0.0F);
        this.spiderLeg7 = new ObjModelRenderer(this);
        this.spiderLeg7.addObjModel(leg, -15.0F, -1.0F, -1.0F, 16, 16, 16, 0.0F);
        this.spiderLeg7.setRotationPoint(-4.0F, 15.0F, -1.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.spiderBody.render(scale);
        this.spiderLeg1.render(scale);
        this.spiderLeg2.render(scale);
        this.spiderLeg3.render(scale);
        this.spiderLeg4.render(scale);
        this.spiderLeg5.render(scale);
        this.spiderLeg6.render(scale);
        this.spiderLeg7.render(scale);
        this.spiderLeg8.render(scale);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        this.spiderLeg1.rotateAngleZ = -((float)Math.PI / 4F);
        this.spiderLeg2.rotateAngleZ = ((float)Math.PI / 4F);
        this.spiderLeg3.rotateAngleZ = -0.58119464F;
        this.spiderLeg4.rotateAngleZ = 0.58119464F;
        this.spiderLeg5.rotateAngleZ = -0.58119464F;
        this.spiderLeg6.rotateAngleZ = 0.58119464F;
        this.spiderLeg7.rotateAngleZ = -((float)Math.PI / 4F);
        this.spiderLeg8.rotateAngleZ = ((float)Math.PI / 4F);
        this.spiderLeg1.rotateAngleY = ((float)Math.PI / 4F);
        this.spiderLeg2.rotateAngleY = -((float)Math.PI / 4F);
        this.spiderLeg3.rotateAngleY = 0.3926991F;
        this.spiderLeg4.rotateAngleY = -0.3926991F;
        this.spiderLeg5.rotateAngleY = -0.3926991F;
        this.spiderLeg6.rotateAngleY = 0.3926991F;
        this.spiderLeg7.rotateAngleY = -((float)Math.PI / 4F);
        this.spiderLeg8.rotateAngleY = ((float)Math.PI / 4F);
        float f3 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
        float f4 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * limbSwingAmount;
        float f5 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
        float f6 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;
        float f7 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
        float f8 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + (float)Math.PI) * 0.4F) * limbSwingAmount;
        float f9 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
        float f10 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float)Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;
        this.spiderLeg1.rotateAngleY += f3;
        this.spiderLeg2.rotateAngleY += -f3;
        this.spiderLeg3.rotateAngleY += f4;
        this.spiderLeg4.rotateAngleY += -f4;
        this.spiderLeg5.rotateAngleY += f5;
        this.spiderLeg6.rotateAngleY += -f5;
        this.spiderLeg7.rotateAngleY += f6;
        this.spiderLeg8.rotateAngleY += -f6;
        this.spiderLeg1.rotateAngleZ += f7;
        this.spiderLeg2.rotateAngleZ += -f7;
        this.spiderLeg3.rotateAngleZ += f8;
        this.spiderLeg4.rotateAngleZ += -f8;
        this.spiderLeg5.rotateAngleZ += f9;
        this.spiderLeg6.rotateAngleZ += -f9;
        this.spiderLeg7.rotateAngleZ += f10;
        this.spiderLeg8.rotateAngleZ += -f10;
    }
    
    public void bake(){
    	testMesh.bake();
        spiderBody.bake();
        spiderLeg1.bake();
        spiderLeg2.bake();
        spiderLeg3.bake();
        spiderLeg4.bake();
        spiderLeg5.bake();
        spiderLeg6.bake();
        spiderLeg7.bake();
        spiderLeg8.bake();
    }
}
