/*******************************************************************************
 * Copyright (C) 2017 Jay Avery
 * 
 * This file is part of Geomastery. Geomastery is free software: distributed
 * under the GNU Affero General Public License (<http://www.gnu.org/licenses/>).
 ******************************************************************************/
package jayavery.geomastery.render.tileentity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Model for box block. */
@SideOnly(Side.CLIENT)
public class ModelBox extends ModelBase {

    /** Model for first side of lid. */
    ModelRenderer lid1 = new ModelRenderer(this, 0, 19).setTextureSize(44, 32);
    /** Model for second side of lid. */
    ModelRenderer lid2 = new ModelRenderer(this, 0, 26).setTextureSize(44, 32);
    /** Model for base. */
    ModelRenderer bottom = new ModelRenderer(this, 0, 0).setTextureSize(44, 32);
    /** Model for knob. */
    ModelRenderer knob = new ModelRenderer(this, 0, 0).setTextureSize(44, 32);
    
    ModelBox() {
        
        this.bottom.addBox(3, 8, 3, 11, 8, 11);
        
        this.knob.addBox(-0.5F, -2, -7, 1, 1, 3);
        this.knob.rotationPointX = 8.5F;
        this.knob.rotationPointY = 8F;
        this.knob.rotationPointZ = 14F;
        
        this.lid1.addBox(-5.5F, -1, -6, 11, 1, 6);
        this.lid1.rotationPointX = 8.5F;
        this.lid1.rotationPointY = 8F;
        this.lid1.rotationPointZ = 14F;
        
        this.lid2.addBox(-5.5F, -1, 0, 11, 1, 5);
        this.lid2.rotationPointX = 8.5F;
        this.lid2.rotationPointY = 8F;
        this.lid2.rotationPointZ = 3F;
    }
    
    void renderAll() {
        
        this.bottom.render(0.0625F);
        this.lid1.render(0.0625F);
        this.lid2.render(0.0625F);
        this.knob.render(0.0625F);
    }
}
