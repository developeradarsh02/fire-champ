export function CenteredSpinner() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-black">
      <div className="w-12 h-12 border-4 border-brand-primary border-t-transparent rounded-full animate-spin" />
    </div>
  )
}

export function InlineSpinner() {
  return (
    <div className="w-5 h-5 border-2 border-brand-primary border-t-transparent rounded-full animate-spin" />
  )
}