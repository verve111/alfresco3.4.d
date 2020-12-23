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
 * DocumentDetails template - DOD5015 extensions.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordDocumentDetails
 */
(function()
{
   /**
    * RecordsDocumentDetails constructor.
    * 
    * @return {Alfresco.RecordsDocumentDetails} The new RecordsDocumentDetails instance
    * @constructor
    */
   Alfresco.RecordsDocumentDetails = function RecordsDocumentDetails_constructor()
   {
      Alfresco.RecordsDocumentDetails.superclass.constructor.call(this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("detailsRefresh", this.onReady, this);

      return this;
   };
   
   YAHOO.extend(Alfresco.RecordsDocumentDetails, Alfresco.DocumentDetails,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RecordsDocumentDetails_onReady()
      {
         var config =
         {
            method: "GET",
            url: Alfresco.constants.PROXY_URI + 'slingshot/doclib/dod5015/node/' + this.options.nodeRef.uri,
            successCallback: 
            { 
               fn: this._getDataSuccess, 
               scope: this 
            },
            failureCallback: 
            { 
               fn: this._getDataFailure, 
               scope: this 
            }
         };
         Alfresco.util.Ajax.request(config);
      }
   });
})();