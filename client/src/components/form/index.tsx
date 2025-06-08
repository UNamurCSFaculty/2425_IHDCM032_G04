import { PhoneField } from './PhoneField'
import { VirtualizedSelectField } from './VirtualizedSelectField'
import { MultiSelectField } from './multi-select-field'
import { NumberField } from './number-field'
import { RadioGroupField } from './RadioGroupField'
import { CheckboxField } from '@/components/form/CheckboxField'
import { DateTimePickerField } from '@/components/form/datetime-field'
import { FileUploadField } from '@/components/form/file-upload-field'
import { ResetButton } from '@/components/form/reset-button'
import { SelectField } from '@/components/form/select-field'
import { SubmitButton } from '@/components/form/submit-button'
import { TextField } from '@/components/form/text-field'
import { TextAreaField } from '@/components/form/textarea-field'
import { createFormHook, createFormHookContexts } from '@tanstack/react-form'
import { RichTextEditorField } from '@/components/form/rich-text-editor-field'
import { CityField } from './CityField'
import { LocationField } from './LocationField'
import { MultiSelectVirtualField } from './multi-select-virtual-field'
import { ReactSelectField } from './react-select-field'

export const { fieldContext, useFieldContext, formContext, useFormContext } =
  createFormHookContexts()

/**
 * Hook pour créer un formulaire avec des composants de champ prédéfinis.
 */
export const { useAppForm } = createFormHook({
  fieldComponents: {
    TextField,
    CheckboxField,
    SelectField,
    MultiSelectField,
    MultiSelectVirtualField,
    ReactSelectField,
    TextAreaField,
    DateTimePickerField,
    FileUploadField,
    NumberField,
    PhoneField,
    VirtualizedSelectField,
    RichTextEditorField,
    RadioGroupField,
    CityField,
    LocationField,
  },
  formComponents: {
    SubmitButton,
    ResetButton,
  },
  fieldContext,
  formContext,
})
