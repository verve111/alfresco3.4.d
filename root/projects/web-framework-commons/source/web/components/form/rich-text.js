/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
 
/**
 * Rich text control component.
 * 
 * This component renders a TinyMCE editor.
 * 
 * @namespace Alfresco
 * @class Alfresco.RichTextControl
 */
(function()
{
   /**
    * RichTextControl constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @param {String} name The name of the component
    * @return {Alfresco.RichTextControl} The new RichTextControl instance
    * @constructor
    */
   Alfresco.RichTextControl = function(htmlId, name)
   {
      // NOTE: This allows us to have a subclass
      var componentName = (typeof name == "undefined" || name === null) ? "Alfresco.RichTextControl" : name;
      
      return Alfresco.RichTextControl.superclass.constructor.call(this, componentName, htmlId, ["button"]);
   };
   
   YAHOO.extend(Alfresco.RichTextControl, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * The current value
          *
          * @property currentValue
          * @type string
          */
         currentValue: "",
         
         /**
          * Flag to determine whether the picker is in disabled mode
          *
          * @property disabled
          * @type boolean
          * @default false
          */
         disabled: false,
         
         /**
          * Flag to indicate whether the field is mandatory
          *
          * @property mandatory
          * @type boolean
          * @default false
          */
         mandatory: false,
         
         /**
          * Object to hold the parameters for the editor
          * 
          * @property editorParameters
          * @type object
          */
         editorParameters: null
      },

      /**
       * The editor instance for the control
       * 
       * @property editor
       * @type object
       */
      editor: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RichTextControl_onReady()
      {
         if (Alfresco.logger.isDebugEnabled())
         {
            Alfresco.logger.debug("Rendering rich text control for element '" + this.id + 
                  "', value = '" + this.options.currentValue + "'");
            Alfresco.logger.debug("Editor parameters for element '" + this.id + "': " + 
                  YAHOO.lang.dump(this.options.editorParameters));
         }

         if (!this.options.disabled)
         {
            // always render the TinyMCE editor for non content properties
            // that are not disabled
            this._renderEditor();
         }
      },
      
      /**
       * Creates and renders the TinyMCE editor
       * 
       * @method _renderEditor
       * @private
       */
      _renderEditor: function RichTextControl__renderEditor()
      {
         // create the editor instance
         this.editor = new Alfresco.util.RichEditor("tinyMCE", this.id, this.options.editorParameters);
      
         // render and register event handler
         this.editor.render();
         this.editor.subscribe("onKeyUp", this._handleContentChange, this, true);
      },
      
      /**
       * Handles the content being changed in the TinyMCE control.
       * 
       * @method _handleContentChange
       * @param type
       * @param args
       * @param obj
       * @private
       */
      _handleContentChange: function RichTextControl__handleContentChange(type, args, obj)
      {
         // save the current contents of the editor to the underlying textarea
         this.editor.save();

         // inform the forms runtime if this field is mandatory
         if (this.options.mandatory)
         {
            YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
         }
      }
   });
})();
