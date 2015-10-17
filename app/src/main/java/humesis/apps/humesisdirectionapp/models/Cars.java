package humesis.apps.humesisdirectionapp.models;

import java.util.List;

/**
 * Created by dhanraj on 17/10/15.
 */
public class Cars {


    /**
     * value : ACURA
     * title : Acura
     * models : [{"value":"CL_MODELS","title":"CL Models (4)"},{"value":"2.2CL","title":" - 2.2CL"},{"value":"2.3CL","title":" - 2.3CL"},{"value":"3.0CL","title":" - 3.0CL"},{"value":"3.2CL","title":" - 3.2CL"},{"value":"ILX","title":"ILX"},{"value":"INTEG","title":"Integra"},{"value":"LEGEND","title":"Legend"},{"value":"MDX","title":"MDX"},{"value":"NSX","title":"NSX"},{"value":"RDX","title":"RDX"},{"value":"RL_MODELS","title":"RL Models (2)"},{"value":"3.5RL","title":" - 3.5 RL"},{"value":"RL","title":" - RL"},{"value":"RSX","title":"RSX"},{"value":"SLX","title":"SLX"},{"value":"TL_MODELS","title":"TL Models (3)"},{"value":"2.5TL","title":" - 2.5TL"},{"value":"3.2TL","title":" - 3.2TL"},{"value":"TL","title":" - TL"},{"value":"TSX","title":"TSX"},{"value":"VIGOR","title":"Vigor"},{"value":"ZDX","title":"ZDX"},{"value":"ACUOTH","title":"Other Acura Models"}]
     */

    private String value;
    private String title;
    private List<ModelsEntity> models;

    public void setValue(String value) {
        this.value = value;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setModels(List<ModelsEntity> models) {
        this.models = models;
    }

    public String getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }

    public List<ModelsEntity> getModels() {
        return models;
    }

    public static class ModelsEntity {
        /**
         * value : CL_MODELS
         * title : CL Models (4)
         */

        private String value;
        private String title;

        public void setValue(String value) {
            this.value = value;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getValue() {
            return value;
        }

        public String getTitle() {
            return title;
        }
    }
}
