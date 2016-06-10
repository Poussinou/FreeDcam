/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.camera1.parameters.manual.mtk;

import android.content.Context;
import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.CameraHolder;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.utils.Logger;

/**
 * Created by GeorgeKiarie on 20/04/2016.
 */
public class AE_Handler_MTK
    {
        public ISOManualParameterMTK isoManualParameter;
        public ShutterManualMtk shutterPrameter;
        private int currentIso = 0;
        private int currentShutter = 0;
        private Parameters parameters;
        boolean auto = true;
        private ParametersHandler parametersHandler;

        final String TAG = AE_Handler_MTK.class.getSimpleName();

        enum AeManual
        {
            shutter,
            iso,
        }

        public AE_Handler_MTK(Context context, Parameters parameters, CameraHolder cameraHolder, ParametersHandler parametersHandler, int maxiso)
        {
            this.parametersHandler = parametersHandler;
            isoManualParameter = new ISOManualParameterMTK(context,parameters,cameraHolder, parametersHandler, aeevent, maxiso);
            shutterPrameter = new ShutterManualMtk(context,parameters, parametersHandler, aeevent);
            this.parameters = parameters;
        }

        public interface AeManualEvent
        {
            void onManualChanged(AeManual fromManual, boolean automode, int value);
        }

        AeManualEvent aeevent =  new AeManualEvent() {
            @Override
            public void onManualChanged(AeManual fromManual, boolean automode, int value)
            {
                if (automode)
                {
                    Logger.d(TAG, "AutomodeActive");
                    auto = automode;


                    switch (fromManual) {
                        case shutter:
                            currentIso = isoManualParameter.GetValue();
                            isoManualParameter.setValue(0);
                            break;
                        case iso:
                            currentShutter = shutterPrameter.GetValue();
                            shutterPrameter.setValue(0);
                            break;
                    }
                }
                else
                {
                    if (auto)
                    {
                        Logger.d(TAG, "Automode Deactivated, set last values");
                        auto = false;
                        switch (fromManual) {
                            case shutter:
                                isoManualParameter.setValue(currentIso);
                                break;
                            case iso:
                                if (currentShutter == 0) currentShutter =9;
                                shutterPrameter.setValue(currentShutter);
                                break;
                        }
                    }
                    else
                    {
                        Logger.d(TAG, "Automode Deactivated, set UserValues");
                        switch (fromManual) {
                            case shutter:
                                shutterPrameter.setValue(value);
                                break;
                            case iso:
                                isoManualParameter.setValue(value);
                                break;
                        }
                    }
                   // parameters.put("lg-manual-mode-reset", "0");
                }
                parametersHandler.SetParametersToCamera(parameters);
                if (automode) {
                    String t = parametersHandler.IsoMode.GetValue();
                    if (!t.equals(KEYS.ISO100))
                        parametersHandler.IsoMode.SetValue(KEYS.ISO100, true);
                    else
                        parametersHandler.IsoMode.SetValue(KEYS.AUTO, true);
                    parametersHandler.IsoMode.SetValue(t, true);
                }
            }
        };
    }