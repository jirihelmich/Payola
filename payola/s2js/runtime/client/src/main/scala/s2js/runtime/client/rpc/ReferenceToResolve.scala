package s2js.runtime.client.rpc

class ReferenceToResolve(val reference: Reference, val sourceObject: Any, val propertyName: String)
{
    def resolve(context: DeserializationContext) {
        val targetObjectId = reference.targetObjectID
        val targetObject = context.objectRegistry.get(targetObjectId)
        if (targetObject.isEmpty) {
            throw new Exception("The deserialized object graph contains an invalid reference '" + targetObjectId + "'.")
        }

        // The reference has to be resolved using eval, because the property name can be nontrivial (array item access).
        s2js.adapters.js.browser.eval("self.sourceObject." + propertyName + " = targetObject.get()")
    }
}
