import { PhoneField } from './PhoneField'
import { VirtualizedSelectField } from './VirtualizedSelectField'
import { NumberField } from './number-field'
import { RadioGroupField } from './RadioGroupField'
import { CheckboxField } from '@/components/form/CheckboxField'
import { AddressField } from '@/components/form/adress-fields'
import { DateTimePickerField } from '@/components/form/datetime-field'
import { FileUploadField } from '@/components/form/file-upload-field'
import { ResetButton } from '@/components/form/reset-button'
import { SelectField } from '@/components/form/select-field'
import { SubmitButton } from '@/components/form/submit-button'
import { TextField } from '@/components/form/text-field'
import { TextAreaField } from '@/components/form/textarea-field'
import { createFormHook, createFormHookContexts } from '@tanstack/react-form'

export const { fieldContext, useFieldContext, formContext, useFormContext } =
  createFormHookContexts()

export const { useAppForm } = createFormHook({
  fieldComponents: {
    TextField,
    CheckboxField,
    SelectField,
    TextAreaField,
    DateTimePickerField,
    FileUploadField,
    AddressField,
    NumberField,
    PhoneField,
    VirtualizedSelectField,
    RadioGroupField,
  },
  formComponents: {
    SubmitButton,
    ResetButton,
  },
  fieldContext,
  formContext,
})
